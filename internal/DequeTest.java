import java.lang.reflect.*;
import java.util.*;

/**
 * A drop-in grading harness for the LinkedBlockDeque exercise.
 *
 * Usage:  put the student's compiled LinkedBlockDequeExercise.class in this
 * directory, then:   javac DequeTest.java && java DequeTest
 *
 * It finds IntDeque by reflection so no submission needs to be edited.
 * Beyond checking return values it also inspects the private block chain
 * (firstBlock / lastBlock / blockSize / count) to verify structural
 * invariants -- block-count bounds and prev/next consistency -- which
 * value-only tests cannot see.
 */
public class DequeTest {

    // ---------- reflective adapter over the student's IntDeque ----------
    static Class<?> cls;
    static Constructor<?> ctor;
    static Method mAddFront, mAddBack, mRemoveFront, mRemoveBack,
                  mPeekFront, mPeekBack, mSize, mIsEmpty, mClear;
    static Field fFirst, fLast, fBlockSize, fCount, fNext, fPrev, fValues;
    static boolean structural = true;

    static void bind(String outerName) throws Exception {
        Class<?> outer = Class.forName(outerName);
        for (Class<?> k : outer.getDeclaredClasses())
            if (k.getSimpleName().equals("IntDeque")) cls = k;
        if (cls == null) cls = Class.forName("IntDeque");
        ctor = cls.getDeclaredConstructor(int.class); ctor.setAccessible(true);
        mAddFront    = m("addFront", int.class);
        mAddBack     = m("addBack", int.class);
        mRemoveFront = m("removeFront");
        mRemoveBack  = m("removeBack");
        mPeekFront   = m("peekFront");
        mPeekBack    = m("peekBack");
        mSize        = m("size");
        mIsEmpty     = m("isEmpty");
        mClear       = m("clear");
        try {
            fFirst = f(cls, "firstBlock"); fLast = f(cls, "lastBlock");
            fBlockSize = f(cls, "blockSize"); fCount = f(cls, "count");
            Class<?> node = fFirst.getType();
            fNext = f(node, "next"); fPrev = f(node, "prev"); fValues = f(node, "values");
        } catch (Exception e) {
            structural = false;
            System.out.println("NOTE: private fields not found in the expected shape; "
                             + "structural invariant checks are disabled.");
        }
    }
    static Method m(String n, Class<?>... p) throws Exception {
        Method x = cls.getDeclaredMethod(n, p); x.setAccessible(true); return x;
    }
    static Field f(Class<?> c, String n) throws Exception {
        Field x = c.getDeclaredField(n); x.setAccessible(true); return x;
    }

    /** Thrown when the submission crashes; aborts the current scenario, not the run. */
    static class Abort extends RuntimeException {
        Abort(String op, Throwable cause) { super(op + " threw " + cause, cause); }
    }
    static Object call(Method m, String name, Object d, Object... args) {
        try { return m.invoke(d, args); }
        catch (InvocationTargetException e) { throw new Abort(name, e.getCause()); }
        catch (Exception e) { throw new Abort(name, e); }
    }

    static Object nw(int blockSize) throws Exception { return ctor.newInstance(blockSize); }
    static boolean addFront(Object d, int v) { return (boolean) call(mAddFront, "addFront(" + v + ")", d, v); }
    static boolean addBack (Object d, int v) { return (boolean) call(mAddBack,  "addBack(" + v + ")", d, v); }
    static int removeFront(Object d) { return (int) call(mRemoveFront, "removeFront()", d); }
    static int removeBack (Object d) { return (int) call(mRemoveBack,  "removeBack()", d); }
    static int peekFront  (Object d) { return (int) call(mPeekFront,   "peekFront()", d); }
    static int peekBack   (Object d) { return (int) call(mPeekBack,    "peekBack()", d); }
    static int size       (Object d) { return (int) call(mSize,        "size()", d); }
    static boolean isEmpty(Object d) { return (boolean) call(mIsEmpty, "isEmpty()", d); }
    static void clear     (Object d) { call(mClear, "clear()", d); }

    /** Runs one scenario; a crash is recorded as a failure and the suite continues. */
    interface Scenario { void run() throws Exception; }
    static void scenario(String tag, Scenario s) {
        try { s.run(); }
        catch (Abort a) { fail(tag + ": CRASHED -- " + a.getMessage()); crashes++; }
        catch (Exception e) { fail(tag + ": unexpected harness error " + e); }
    }
    static int crashes = 0;

    // ---------- reporting ----------
    static String section = "";
    static int pass = 0;
    static Map<String, List<String>> failures = new LinkedHashMap<>();

    static void section(String s) { section = s; }

    static void fail(String msg) {
        failures.computeIfAbsent(section, k -> new ArrayList<>()).add(msg);
    }
    static void eq(String what, int actual, int expected) {
        if (actual == expected) pass++;
        else fail(what + ": expected " + expected + ", got " + actual);
    }
    static void eq(String what, boolean actual, boolean expected) {
        if (actual == expected) pass++;
        else fail(what + ": expected " + expected + ", got " + actual);
    }
    static void ok(String what, boolean cond) {
        if (cond) pass++; else fail(what);
    }

    // ---------- structural invariants ----------
    /** Walk the block chain; verify link consistency and that block count is bounded. */
    static void invariants(String where, Object d) {
        if (!structural) return;
        try {
            Object first = fFirst.get(d), last = fLast.get(d);
            int n = fCount.getInt(d), bs = fBlockSize.getInt(d);

            if (n == 0) {
                // an empty deque need not hold blocks, but if it does they must be consistent
                if (first == null ^ last == null)
                    fail(where + ": empty deque has firstBlock/lastBlock disagreeing about null");
                else pass++;
                return;
            }
            if (first == null || last == null) {
                fail(where + ": size " + n + " but firstBlock/lastBlock is null");
                return;
            }
            if (fPrev.get(first) != null) fail(where + ": firstBlock.prev is not null"); else pass++;
            if (fNext.get(last)  != null) fail(where + ": lastBlock.next is not null");  else pass++;

            int blocks = 0;
            Object cur = first, prev = null;
            Set<Object> seen = Collections.newSetFromMap(new IdentityHashMap<>());
            while (cur != null) {
                if (!seen.add(cur)) { fail(where + ": block chain contains a cycle"); return; }
                if (fPrev.get(cur) != prev) {
                    fail(where + ": block " + blocks + " prev pointer does not match the chain");
                    return;
                }
                if (((int[]) fValues.get(cur)).length != bs) {
                    fail(where + ": block " + blocks + " has length "
                         + ((int[]) fValues.get(cur)).length + ", expected blockSize " + bs);
                    return;
                }
                blocks++;
                if (blocks > 100000) { fail(where + ": block chain is implausibly long"); return; }
                prev = cur; cur = fNext.get(cur);
            }
            if (prev != last) { fail(where + ": walking next from firstBlock does not reach lastBlock"); return; }
            pass++;

            // Space efficiency: n elements need at least ceil(n/bs) blocks; allow 2 slack
            // for a partially-consumed block at each end.
            int minBlocks = (n + bs - 1) / bs;
            if (blocks > minBlocks + 2)
                fail(where + ": " + blocks + " blocks allocated for " + n
                     + " elements with blockSize " + bs + " (expected at most " + (minBlocks + 2)
                     + ") -- blocks are being wasted or never reused");
            else pass++;
        } catch (Exception e) {
            fail(where + ": exception while inspecting internals: " + e);
        }
    }

    // ---------- a mirror that checks size/isEmpty after EVERY operation ----------
    static class Mirror {
        Object d; ArrayDeque<Integer> ref = new ArrayDeque<>(); int bs; String tag;
        Mirror(int bs, String tag) throws Exception { this.bs = bs; this.tag = tag; d = nw(bs); }

        void addFront(int v) throws Exception {
            boolean r = DequeTest.addFront(d, v); ref.addFirst(v);
            if (!r) fail(tag + ": addFront(" + v + ") returned false"); else pass++;
            sync("after addFront(" + v + ")");
        }
        void addBack(int v) throws Exception {
            boolean r = DequeTest.addBack(d, v); ref.addLast(v);
            if (!r) fail(tag + ": addBack(" + v + ") returned false"); else pass++;
            sync("after addBack(" + v + ")");
        }
        void removeFront() throws Exception {
            int got = DequeTest.removeFront(d);
            int exp = ref.isEmpty() ? -1 : ref.removeFirst();
            eq(tag + ": removeFront", got, exp);
            sync("after removeFront");
        }
        void removeBack() throws Exception {
            int got = DequeTest.removeBack(d);
            int exp = ref.isEmpty() ? -1 : ref.removeLast();
            eq(tag + ": removeBack", got, exp);
            sync("after removeBack");
        }
        void clear() throws Exception { DequeTest.clear(d); ref.clear(); sync("after clear"); }

        void sync(String where) {
            {
                int s = size(d);
                if (s != ref.size()) fail(tag + " " + where + ": size " + s + ", expected " + ref.size());
                else pass++;
                boolean e = isEmpty(d);
                if (e != ref.isEmpty()) fail(tag + " " + where + ": isEmpty " + e + ", expected " + ref.isEmpty());
                else pass++;
                int pf = peekFront(d), pb = peekBack(d);
                int epf = ref.isEmpty() ? -1 : ref.peekFirst();
                int epb = ref.isEmpty() ? -1 : ref.peekLast();
                if (pf != epf) fail(tag + " " + where + ": peekFront " + pf + ", expected " + epf); else pass++;
                if (pb != epb) fail(tag + " " + where + ": peekBack "  + pb + ", expected " + epb); else pass++;
            }
            invariants(tag + " " + where, d);
        }
    }

    // ---------- the tests ----------
    static final int[] SIZES = {1, 2, 3, 4, 5, 8};

    static void testEmptyContract() throws Exception {
        section("A. empty-deque contract");
        for (int bs : SIZES) {
            String t = "bs=" + bs;
            scenario(t, () -> {
            Object d = nw(bs);
            eq(t + " fresh isEmpty", isEmpty(d), true);
            eq(t + " fresh size", size(d), 0);
            eq(t + " fresh peekFront", peekFront(d), -1);
            eq(t + " fresh peekBack", peekBack(d), -1);
            eq(t + " fresh removeFront", removeFront(d), -1);
            eq(t + " fresh removeBack", removeBack(d), -1);
            // repeated removes on empty must stay harmless
            for (int i = 0; i < 5; i++) { removeFront(d); removeBack(d); }
            eq(t + " still empty after repeated removes", size(d), 0);
            invariants(t + " empty", d);
            });
        }
    }

    static void testSingleElementRoundTrips() throws Exception {
        section("B. single element, all four add/remove pairings");
        for (int bs : SIZES) {
            for (int addAtFront = 0; addAtFront < 2; addAtFront++) {
                for (int removeAtFront = 0; removeAtFront < 2; removeAtFront++) {
                    String t = "bs=" + bs + " add" + (addAtFront == 1 ? "Front" : "Back")
                             + "/remove" + (removeAtFront == 1 ? "Front" : "Back");
                    final int af = addAtFront, rf = removeAtFront;
                    scenario(t, () -> {
                    Mirror m = new Mirror(bs, t);
                    for (int rep = 0; rep < 3; rep++) {       // repeat: catches stale state after drain
                        if (af == 1) m.addFront(42); else m.addBack(42);
                        if (rf == 1) m.removeFront(); else m.removeBack();
                        eq(t + " empty after round trip " + rep, isEmpty(m.d), true);
                    }
                    });
                }
            }
        }
    }

    static void testBoundaryCrossing() throws Exception {
        section("C. block-boundary crossing, all four fill/drain directions");
        for (int bs : SIZES) {
            int n = bs * 3 + 1;                  // guarantees several blocks plus a partial one
            for (int fillFront = 0; fillFront < 2; fillFront++) {
                for (int drainFront = 0; drainFront < 2; drainFront++) {
                    String t = "bs=" + bs + " fill" + (fillFront == 1 ? "Front" : "Back")
                             + "/drain" + (drainFront == 1 ? "Front" : "Back");
                    final int ff = fillFront, df = drainFront;
                    scenario(t, () -> {
                    Mirror m = new Mirror(bs, t);
                    for (int i = 0; i < n; i++) {
                        if (ff == 1) m.addFront(i); else m.addBack(i);
                    }
                    for (int i = 0; i < n; i++) {
                        if (df == 1) m.removeFront(); else m.removeBack();
                    }
                    eq(t + " empty after drain", isEmpty(m.d), true);
                    eq(t + " size 0 after drain", size(m.d), 0);
                    });
                }
            }
        }
    }

    static void testExactBlockFill() throws Exception {
        section("D. exact block boundaries (n is a precise multiple of blockSize)");
        for (int bs : SIZES) {
            for (int blocks = 1; blocks <= 4; blocks++) {
                String t = "bs=" + bs + " exactly " + blocks + " full block(s)";
                final int bl = blocks;
                scenario(t, () -> {
                Mirror m = new Mirror(bs, t);
                for (int i = 0; i < bs * bl; i++) m.addBack(i);
                for (int i = 0; i < bs * bl; i++) m.removeFront();
                eq(t + " empty", isEmpty(m.d), true);

                Mirror m2 = new Mirror(bs, t + " (front)");
                for (int i = 0; i < bs * bl; i++) m2.addFront(i);
                for (int i = 0; i < bs * bl; i++) m2.removeBack();
                eq(t + " (front) empty", isEmpty(m2.d), true);
                });
            }
        }
    }

    static void testPingPongAtBoundary() throws Exception {
        section("E. add/remove thrashing exactly on a block boundary");
        for (int bs : SIZES) {
            for (int fill : new int[]{bs - 1, bs, bs + 1, 2 * bs, 2 * bs + 1}) {
                if (fill < 0) continue;
                String t = "bs=" + bs + " preload=" + fill;
                final int fl = fill;
                scenario(t, () -> {
                Mirror m = new Mirror(bs, t + " back-thrash");
                for (int i = 0; i < fl; i++) m.addBack(i);
                for (int i = 0; i < 6; i++) { m.addBack(900 + i); m.removeBack(); }
                for (int i = 0; i < 6; i++) { m.addFront(800 + i); m.removeFront(); }
                // and cross-end: add at one end, remove at the other
                for (int i = 0; i < 6; i++) { m.addBack(700 + i); m.removeFront(); }
                for (int i = 0; i < 6; i++) { m.addFront(600 + i); m.removeBack(); }
                while (size(m.d) > 0) m.removeFront();
                });
            }
        }
    }

    static void testInterleavedGrowth() throws Exception {
        section("F. interleaved growth at both ends");
        for (int bs : SIZES) {
            scenario("bs=" + bs + " interleaved", () -> {
            Mirror m = new Mirror(bs, "bs=" + bs + " interleaved");
            for (int i = 0; i < 40; i++) { m.addFront(-i); m.addBack(i); }
            for (int i = 0; i < 20; i++) { m.removeFront(); m.removeBack(); }
            for (int i = 0; i < 40; i++) { m.addBack(1000 + i); m.removeFront(); }
            while (size(m.d) > 0) m.removeBack();
            eq("bs=" + bs + " interleaved empty", isEmpty(m.d), true);
            });
        }
    }

    static void testValueTransparency() throws Exception {
        section("G. values are stored verbatim (0, negatives, duplicates, MIN/MAX)");
        int[] vals = {0, 0, 0, -1, -7, Integer.MIN_VALUE, Integer.MAX_VALUE, 5, 5, 0};
        for (int bs : SIZES) {
            scenario("bs=" + bs + " values", () -> {
            Mirror m = new Mirror(bs, "bs=" + bs + " values");
            for (int v : vals) m.addBack(v);
            // NOTE: -1 doubles as the "empty" sentinel, so a -1 coming back is only
            // meaningful because size() is checked alongside it on every step.
            for (int i = 0; i < vals.length; i++) m.removeFront();
            eq("bs=" + bs + " values drained", isEmpty(m.d), true);

            Mirror m2 = new Mirror(bs, "bs=" + bs + " zeros only");
            for (int i = 0; i < bs * 3 + 2; i++) m2.addFront(0);
            eq("bs=" + bs + " size with all-zero payload", size(m2.d), bs * 3 + 2);
            while (size(m2.d) > 0) m2.removeBack();
            });
        }
    }

    static void testClearAndReuse() throws Exception {
        section("H. clear() and reuse");
        for (int bs : SIZES) {
            scenario("bs=" + bs + " clear/reuse", () -> {
            Mirror m = new Mirror(bs, "bs=" + bs + " clear/reuse");
            for (int round = 0; round < 3; round++) {
                for (int i = 0; i < bs * 2 + 1; i++) m.addBack(i);
                m.clear();
                eq("bs=" + bs + " empty after clear", isEmpty(m.d), true);
                eq("bs=" + bs + " peekFront after clear", peekFront(m.d), -1);
                eq("bs=" + bs + " removeBack after clear", removeBack(m.d), -1);
                // must be fully usable again
                for (int i = 0; i < bs + 1; i++) m.addFront(i);
                while (size(m.d) > 0) m.removeFront();
            }
            });
        }
    }

    static void testMemoryReuse() throws Exception {
        section("I. blocks are released as the deque shrinks");
        if (!structural) { System.out.println("   (skipped -- internals not visible)"); return; }
        for (int bs : SIZES) {
            scenario("bs=" + bs + " grow/shrink", () -> {
            Mirror m = new Mirror(bs, "bs=" + bs + " grow/shrink");
            for (int i = 0; i < bs * 50; i++) m.addBack(i);
            for (int i = 0; i < bs * 49; i++) m.removeFront();
            invariants("bs=" + bs + " after shrink from 50 blocks to ~1", m.d);
            // repeated front growth is where a per-add allocation bug shows up
            Mirror m2 = new Mirror(bs, "bs=" + bs + " front growth");
            for (int i = 0; i < bs * 20; i++) m2.addFront(i);
            invariants("bs=" + bs + " after " + (bs * 20) + " addFront calls", m2.d);
            });
        }
    }

    /** Differential testing against ArrayDeque, with shrinking to a minimal failing script. */
    static void testRandomized() throws Exception {
        section("J. randomized differential test vs java.util.ArrayDeque");
        for (int bs : SIZES) {
            for (int seed = 1; seed <= 4; seed++) {
                List<int[]> script = buildScript(new Random(seed * 1000 + bs), 4000);
                String err = run(bs, script);
                if (err == null) { pass++; continue; }
                List<int[]> minimal = shrink(bs, script);
                fail("bs=" + bs + " seed=" + seed + ": " + err
                     + "\n        minimal reproducing sequence (" + minimal.size() + " ops): "
                     + render(minimal));
            }
        }
    }

    static List<int[]> buildScript(Random r, int n) {
        List<int[]> s = new ArrayList<>();
        for (int i = 0; i < n; i++) s.add(new int[]{r.nextInt(6), r.nextInt(200) - 100});
        return s;
    }

    /** Replays a script; returns null on success or a description of the first divergence. */
    static String run(int bs, List<int[]> script) throws Exception {
        Object d;
        try { d = nw(bs); } catch (Exception e) { return "constructor threw " + e; }
        ArrayDeque<Integer> ref = new ArrayDeque<>();
        for (int i = 0; i < script.size(); i++) {
            int op = script.get(i)[0], v = script.get(i)[1];
            int got, exp;
            try {
                switch (op) {
                    case 0: addFront(d, v); ref.addFirst(v); break;
                    case 1: addBack(d, v);  ref.addLast(v);  break;
                    case 2: got = removeFront(d); exp = ref.isEmpty() ? -1 : ref.removeFirst();
                            if (got != exp) return "op " + i + " removeFront gave " + got + ", expected " + exp;
                            break;
                    case 3: got = removeBack(d);  exp = ref.isEmpty() ? -1 : ref.removeLast();
                            if (got != exp) return "op " + i + " removeBack gave " + got + ", expected " + exp;
                            break;
                    case 4: got = peekFront(d);   exp = ref.isEmpty() ? -1 : ref.peekFirst();
                            if (got != exp) return "op " + i + " peekFront gave " + got + ", expected " + exp;
                            break;
                    default: got = peekBack(d);   exp = ref.isEmpty() ? -1 : ref.peekLast();
                            if (got != exp) return "op " + i + " peekBack gave " + got + ", expected " + exp;
                }
                if (size(d) != ref.size())
                    return "op " + i + " left size " + size(d) + ", expected " + ref.size();
                if (isEmpty(d) != ref.isEmpty())
                    return "op " + i + " left isEmpty " + isEmpty(d) + ", expected " + ref.isEmpty();
            } catch (Abort a) {
                return "op " + i + " " + a.getMessage();
            }
        }
        return null;
    }

    /** Delta-debugging: drop operations while the failure survives. */
    static List<int[]> shrink(int bs, List<int[]> script) throws Exception {
        List<int[]> best = script;
        int chunk = best.size() / 2;
        while (chunk >= 1) {
            boolean progress = false;
            for (int start = 0; start + chunk <= best.size(); ) {
                List<int[]> candidate = new ArrayList<>(best);
                candidate.subList(start, start + chunk).clear();
                if (candidate.size() > 0 && run(bs, candidate) != null) {
                    best = candidate; progress = true;
                } else {
                    start += chunk;
                }
                if (best.size() < 2) return best;
            }
            if (!progress) chunk /= 2;
        }
        return best;
    }

    static String render(List<int[]> s) {
        String[] names = {"addFront", "addBack", "removeFront", "removeBack", "peekFront", "peekBack"};
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.size() && i < 40; i++) {
            if (i > 0) sb.append("; ");
            int op = s.get(i)[0];
            sb.append(names[op]).append(op < 2 ? "(" + s.get(i)[1] + ")" : "()");
        }
        if (s.size() > 40) sb.append("; ... (" + (s.size() - 40) + " more)");
        return sb.toString();
    }

    public static void main(String[] args) throws Exception {
        String target = args.length > 0 ? args[0] : "LinkedBlockDequeExercise";
        bind(target);
        System.out.println("Testing IntDeque from " + target + "\n");

        testEmptyContract();
        testSingleElementRoundTrips();
        testBoundaryCrossing();
        testExactBlockFill();
        testPingPongAtBoundary();
        testInterleavedGrowth();
        testValueTransparency();
        testClearAndReuse();
        testMemoryReuse();
        testRandomized();

        int failCount = failures.values().stream().mapToInt(List::size).sum();
        System.out.println("================ RESULTS ================");
        System.out.println(pass + " checks passed, " + failCount + " failed");
        if (failCount == 0) {
            System.out.println("\nAll sections clean.");
        } else {
            for (var e : failures.entrySet()) {
                System.out.println("\n--- " + e.getKey() + " (" + e.getValue().size() + " failures) ---");
                int shown = 0;
                for (String s : e.getValue()) {
                    if (shown++ == 6) { System.out.println("      ... and " + (e.getValue().size() - 6) + " more"); break; }
                    System.out.println("   " + s);
                }
            }
        }
    }
}
