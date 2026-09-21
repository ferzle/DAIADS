"""
A drop-in grading harness for the LinkedBlockDeque exercise (Python version).

Usage:  python3 deque_test.py <student_file.py>

Loads the student's module, finds IntDeque, and exercises it far beyond the
handout's test.  Besides checking return values it inspects the private block
chain (first_block / last_block / block_size / count) to verify structural
invariants -- link consistency and block-count bounds -- which value-only
tests cannot see.
"""
import sys, os, random, collections, importlib.util, traceback

# ---------------------------------------------------------------- loading
def load(path):
    src = open(path, encoding="utf-8").read()
    # drop bare top-level calls to the handout's own test so importing is silent
    src = "\n".join(l for l in src.split("\n")
                    if l.strip() not in ("test_deque()", "testDeque()", "main()"))
    ns = {"__name__": "student"}
    exec(compile(src, path, "exec"), ns)
    for v in ns.values():
        if isinstance(v, type) and v.__name__ == "IntDeque":
            return v
    raise SystemExit("No class named IntDeque found in " + path)


# ---------------------------------------------------------------- reporting
SECTION = [""]
PASS = [0]
FAILURES = {}

def section(s): SECTION[0] = s
def fail(msg): FAILURES.setdefault(SECTION[0], []).append(msg)
def eq(what, actual, expected):
    if actual == expected: PASS[0] += 1
    else: fail(f"{what}: expected {expected!r}, got {actual!r}")

class Abort(Exception): pass

def scenario(tag, fn):
    try:
        fn()
    except Abort:
        raise
    except Exception as e:
        fail(f"{tag}: CRASHED -- {type(e).__name__}: {e}")


# ---------------------------------------------------------------- adapter
class D:
    """Wraps the student's deque; normalizes naming and turns crashes into Abort."""
    def __init__(self, cls, bs):
        self.d = cls(bs)
    def _c(self, name, *a):
        f = getattr(self.d, name)
        try: return f(*a)
        except Exception as e:
            raise Abort(f"{name}({', '.join(map(repr, a))}) raised {type(e).__name__}: {e}")
    def add_front(self, v): return self._c("add_front", v)
    def add_back(self, v):  return self._c("add_back", v)
    def remove_front(self): return self._c("remove_front")
    def remove_back(self):  return self._c("remove_back")
    def peek_front(self):   return self._c("peek_front")
    def peek_back(self):    return self._c("peek_back")
    def size(self):         return self._c("size")
    def is_empty(self):     return self._c("is_empty")
    def clear(self):        return self._c("clear")


# ---------------------------------------------------------------- invariants
def invariants(where, w):
    d = w.d
    try:
        first = getattr(d, "first_block"); last = getattr(d, "last_block")
        n = getattr(d, "count"); bs = getattr(d, "block_size")
    except AttributeError:
        return
    if n == 0:
        if (first is None) != (last is None):
            fail(f"{where}: empty deque has first_block/last_block disagreeing about None")
        else: PASS[0] += 1
        return
    if first is None or last is None:
        fail(f"{where}: size {n} but first_block/last_block is None"); return
    if first.prev is not None: fail(f"{where}: first_block.prev is not None")
    else: PASS[0] += 1
    if last.next is not None: fail(f"{where}: last_block.next is not None")
    else: PASS[0] += 1

    blocks, cur, prev, seen = 0, first, None, set()
    while cur is not None:
        if id(cur) in seen: fail(f"{where}: block chain contains a cycle"); return
        seen.add(id(cur))
        if cur.prev is not prev:
            fail(f"{where}: block {blocks} prev pointer does not match the chain"); return
        if len(cur.values) != bs:
            fail(f"{where}: block {blocks} holds {len(cur.values)} slots, expected block_size {bs}")
            return
        blocks += 1
        if blocks > 100000: fail(f"{where}: block chain is implausibly long"); return
        prev, cur = cur, cur.next
    if prev is not last:
        fail(f"{where}: walking next from first_block does not reach last_block"); return
    PASS[0] += 1
    min_blocks = -(-n // bs)
    if blocks > min_blocks + 2:
        fail(f"{where}: {blocks} blocks allocated for {n} elements with block_size {bs} "
             f"(expected at most {min_blocks + 2}) -- blocks wasted or never reused")
    else: PASS[0] += 1


# ---------------------------------------------------------------- mirror
class Mirror:
    """Every operation is checked against collections.deque, plus size/is_empty/peeks after each."""
    def __init__(self, cls, bs, tag):
        self.w = D(cls, bs); self.ref = collections.deque(); self.tag = tag
    @property
    def d(self): return self.w
    def add_front(self, v):
        r = self.w.add_front(v); self.ref.appendleft(v)
        if r is not True: fail(f"{self.tag}: add_front({v}) returned {r!r}, expected True")
        else: PASS[0] += 1
        self.sync(f"after add_front({v})")
    def add_back(self, v):
        r = self.w.add_back(v); self.ref.append(v)
        if r is not True: fail(f"{self.tag}: add_back({v}) returned {r!r}, expected True")
        else: PASS[0] += 1
        self.sync(f"after add_back({v})")
    def remove_front(self):
        got = self.w.remove_front()
        exp = self.ref.popleft() if self.ref else -1
        eq(f"{self.tag}: remove_front", got, exp); self.sync("after remove_front")
    def remove_back(self):
        got = self.w.remove_back()
        exp = self.ref.pop() if self.ref else -1
        eq(f"{self.tag}: remove_back", got, exp); self.sync("after remove_back")
    def clear(self):
        self.w.clear(); self.ref.clear(); self.sync("after clear")
    def sync(self, where):
        eq(f"{self.tag} {where}: size", self.w.size(), len(self.ref))
        eq(f"{self.tag} {where}: is_empty", self.w.is_empty(), not self.ref)
        eq(f"{self.tag} {where}: peek_front", self.w.peek_front(), self.ref[0] if self.ref else -1)
        eq(f"{self.tag} {where}: peek_back", self.w.peek_back(), self.ref[-1] if self.ref else -1)
        invariants(f"{self.tag} {where}", self.w)


SIZES = [1, 2, 3, 4, 5, 8]

# ---------------------------------------------------------------- tests
def test_empty(cls):
    section("A. empty-deque contract")
    for bs in SIZES:
        t = f"bs={bs}"
        def body(bs=bs, t=t):
            w = D(cls, bs)
            eq(t + " fresh is_empty", w.is_empty(), True)
            eq(t + " fresh size", w.size(), 0)
            eq(t + " fresh peek_front", w.peek_front(), -1)
            eq(t + " fresh peek_back", w.peek_back(), -1)
            eq(t + " fresh remove_front", w.remove_front(), -1)
            eq(t + " fresh remove_back", w.remove_back(), -1)
            for _ in range(5): w.remove_front(); w.remove_back()
            eq(t + " still empty after repeated removes", w.size(), 0)
            invariants(t + " empty", w)
        scenario(t, body)

def test_single(cls):
    section("B. single element, all four add/remove pairings")
    for bs in SIZES:
        for af in (0, 1):
            for rf in (0, 1):
                t = f"bs={bs} add{'Front' if af else 'Back'}/remove{'Front' if rf else 'Back'}"
                def body(bs=bs, af=af, rf=rf, t=t):
                    m = Mirror(cls, bs, t)
                    for rep in range(3):
                        (m.add_front if af else m.add_back)(42)
                        (m.remove_front if rf else m.remove_back)()
                        eq(f"{t} empty after round trip {rep}", m.w.is_empty(), True)
                scenario(t, body)

def test_boundary(cls):
    section("C. block-boundary crossing, all four fill/drain directions")
    for bs in SIZES:
        n = bs * 3 + 1
        for ff in (0, 1):
            for df in (0, 1):
                t = f"bs={bs} fill{'Front' if ff else 'Back'}/drain{'Front' if df else 'Back'}"
                def body(bs=bs, ff=ff, df=df, n=n, t=t):
                    m = Mirror(cls, bs, t)
                    for i in range(n): (m.add_front if ff else m.add_back)(i)
                    for i in range(n): (m.remove_front if df else m.remove_back)()
                    eq(t + " empty after drain", m.w.is_empty(), True)
                    eq(t + " size 0 after drain", m.w.size(), 0)
                scenario(t, body)

def test_exact_fill(cls):
    section("D. exact block boundaries (n a precise multiple of block_size)")
    for bs in SIZES:
        for blocks in range(1, 5):
            t = f"bs={bs} exactly {blocks} full block(s)"
            def body(bs=bs, blocks=blocks, t=t):
                m = Mirror(cls, bs, t)
                for i in range(bs * blocks): m.add_back(i)
                for i in range(bs * blocks): m.remove_front()
                eq(t + " empty", m.w.is_empty(), True)
                m2 = Mirror(cls, bs, t + " (front)")
                for i in range(bs * blocks): m2.add_front(i)
                for i in range(bs * blocks): m2.remove_back()
                eq(t + " (front) empty", m2.w.is_empty(), True)
            scenario(t, body)

def test_pingpong(cls):
    section("E. add/remove thrashing exactly on a block boundary")
    for bs in SIZES:
        for fill in (bs - 1, bs, bs + 1, 2 * bs, 2 * bs + 1):
            if fill < 0: continue
            t = f"bs={bs} preload={fill}"
            def body(bs=bs, fill=fill, t=t):
                m = Mirror(cls, bs, t)
                for i in range(fill): m.add_back(i)
                for i in range(6): m.add_back(900 + i); m.remove_back()
                for i in range(6): m.add_front(800 + i); m.remove_front()
                for i in range(6): m.add_back(700 + i); m.remove_front()
                for i in range(6): m.add_front(600 + i); m.remove_back()
                while m.w.size() > 0: m.remove_front()
            scenario(t, body)

def test_interleaved(cls):
    section("F. interleaved growth at both ends")
    for bs in SIZES:
        t = f"bs={bs} interleaved"
        def body(bs=bs, t=t):
            m = Mirror(cls, bs, t)
            for i in range(40): m.add_front(-i); m.add_back(i)
            for i in range(20): m.remove_front(); m.remove_back()
            for i in range(40): m.add_back(1000 + i); m.remove_front()
            while m.w.size() > 0: m.remove_back()
            eq(t + " empty", m.w.is_empty(), True)
        scenario(t, body)

def test_values(cls):
    section("G. values stored verbatim (0, negatives, duplicates, extremes)")
    vals = [0, 0, 0, -1, -7, -2**31, 2**31 - 1, 5, 5, 0]
    for bs in SIZES:
        t = f"bs={bs} values"
        def body(bs=bs, t=t):
            m = Mirror(cls, bs, t)
            for v in vals: m.add_back(v)
            for _ in vals: m.remove_front()
            eq(t + " drained", m.w.is_empty(), True)
            m2 = Mirror(cls, bs, f"bs={bs} zeros only")
            for _ in range(bs * 3 + 2): m2.add_front(0)
            eq(f"bs={bs} size with all-zero payload", m2.w.size(), bs * 3 + 2)
            while m2.w.size() > 0: m2.remove_back()
        scenario(t, body)

def test_clear_reuse(cls):
    section("H. clear() and reuse")
    for bs in SIZES:
        t = f"bs={bs} clear/reuse"
        def body(bs=bs, t=t):
            m = Mirror(cls, bs, t)
            for _ in range(3):
                for i in range(bs * 2 + 1): m.add_back(i)
                m.clear()
                eq(t + " empty after clear", m.w.is_empty(), True)
                eq(t + " peek_front after clear", m.w.peek_front(), -1)
                eq(t + " remove_back after clear", m.w.remove_back(), -1)
                for i in range(bs + 1): m.add_front(i)
                while m.w.size() > 0: m.remove_front()
        scenario(t, body)

def test_memory(cls):
    section("I. blocks are released as the deque shrinks")
    for bs in SIZES:
        t = f"bs={bs} grow/shrink"
        def body(bs=bs, t=t):
            m = Mirror(cls, bs, t)
            for i in range(bs * 50): m.add_back(i)
            for i in range(bs * 49): m.remove_front()
            invariants(f"bs={bs} after shrinking from 50 blocks to ~1", m.w)
            m2 = Mirror(cls, bs, f"bs={bs} front growth")
            for i in range(bs * 20): m2.add_front(i)
            invariants(f"bs={bs} after {bs*20} add_front calls", m2.w)
        scenario(t, body)


# ---------------------------------------------------------------- randomized
OPS = ["add_front", "add_back", "remove_front", "remove_back", "peek_front", "peek_back"]

def replay(cls, bs, script):
    """Returns None on success, else a description of the first divergence."""
    try: w = D(cls, bs)
    except Exception as e: return f"constructor raised {e}"
    ref = collections.deque()
    for i, (op, v) in enumerate(script):
        try:
            if op == 0: w.add_front(v); ref.appendleft(v)
            elif op == 1: w.add_back(v); ref.append(v)
            elif op == 2:
                got = w.remove_front(); exp = ref.popleft() if ref else -1
                if got != exp: return f"op {i} remove_front gave {got!r}, expected {exp!r}"
            elif op == 3:
                got = w.remove_back(); exp = ref.pop() if ref else -1
                if got != exp: return f"op {i} remove_back gave {got!r}, expected {exp!r}"
            elif op == 4:
                got = w.peek_front(); exp = ref[0] if ref else -1
                if got != exp: return f"op {i} peek_front gave {got!r}, expected {exp!r}"
            else:
                got = w.peek_back(); exp = ref[-1] if ref else -1
                if got != exp: return f"op {i} peek_back gave {got!r}, expected {exp!r}"
            if w.size() != len(ref): return f"op {i} left size {w.size()}, expected {len(ref)}"
            if w.is_empty() != (not ref): return f"op {i} left is_empty {w.is_empty()}"
        except Abort as a:
            return f"op {i}: {a}"
    return None

def shrink(cls, bs, script):
    """Delta-debugging: drop operations while the failure survives."""
    best, chunk = script, len(script) // 2
    while chunk >= 1:
        progress, start = False, 0
        while start + chunk <= len(best):
            cand = best[:start] + best[start + chunk:]
            if cand and replay(cls, bs, cand) is not None:
                best, progress = cand, True
            else:
                start += chunk
            if len(best) < 2: return best
        if not progress: chunk //= 2
    return best

def render(script):
    out = []
    for op, v in script[:40]:
        out.append(f"{OPS[op]}({v})" if op < 2 else f"{OPS[op]}()")
    if len(script) > 40: out.append(f"... ({len(script)-40} more)")
    return "; ".join(out)

def test_random(cls):
    section("J. randomized differential test vs collections.deque")
    for bs in SIZES:
        for seed in range(1, 5):
            r = random.Random(seed * 1000 + bs)
            script = [(r.randrange(6), r.randrange(-100, 100)) for _ in range(4000)]
            err = replay(cls, bs, script)
            if err is None:
                PASS[0] += 1; continue
            mini = shrink(cls, bs, script)
            fail(f"bs={bs} seed={seed}: {err}\n        minimal reproducing sequence "
                 f"({len(mini)} ops): {render(mini)}")


def main():
    path = sys.argv[1] if len(sys.argv) > 1 else "LinkedBlockDeque.py"
    cls = load(path)
    print(f"Testing IntDeque from {os.path.basename(path)}\n")
    for t in (test_empty, test_single, test_boundary, test_exact_fill, test_pingpong,
              test_interleaved, test_values, test_clear_reuse, test_memory, test_random):
        try: t(cls)
        except Abort as a: fail(f"section aborted: {a}")
        except Exception:
            fail("harness error:\n" + traceback.format_exc())
    n_fail = sum(len(v) for v in FAILURES.values())
    print("================ RESULTS ================")
    print(f"{PASS[0]} checks passed, {n_fail} failed")
    if not n_fail:
        print("\nAll sections clean.")
    else:
        for sec, msgs in FAILURES.items():
            print(f"\n--- {sec} ({len(msgs)} failures) ---")
            for msg in msgs[:6]: print("   " + msg)
            if len(msgs) > 6: print(f"      ... and {len(msgs)-6} more")

main()
