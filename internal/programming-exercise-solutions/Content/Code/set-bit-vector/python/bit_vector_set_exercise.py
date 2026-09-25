class BitVectorIntSet:
    BITS_PER_WORD = 32

    def __init__(self, universe_size):
        if universe_size < 1:
            raise ValueError("universe_size must be positive")
        self._universe_size = universe_size
        word_count = (universe_size + self.BITS_PER_WORD - 1) // self.BITS_PER_WORD
        self._words = [0] * word_count
        self._count = 0

    # Helpers

    def _word_index(self, key):
        return key // self.BITS_PER_WORD

    def _bit_index(self, key):
        return key % self.BITS_PER_WORD

    def _word_mask(self, bit_index):
        return 1 << bit_index

    def _check_key(self, key):
        if key < 0 or key >= self._universe_size:
            raise IndexError(f"key must be in [0, {self._universe_size})")

    def universe_size(self):
        return self._universe_size

    def is_empty(self):
        return self._count == 0

    def size(self):
        return self._count

    def clear(self):
        for i in range(len(self._words)):
            self._words[i] = 0
        self._count = 0

    def contains(self, key):
        self._check_key(key)
        mask = self._word_mask(self._bit_index(key))
        return self._words[self._word_index(key)] & mask != 0

    def add(self, key):
        self._check_key(key)
        word = self._word_index(key)
        mask = self._word_mask(self._bit_index(key))
        if self._words[word] & mask:
            return False
        self._words[word] |= mask
        self._count += 1
        return True

    def remove(self, key):
        self._check_key(key)
        word = self._word_index(key)
        mask = self._word_mask(self._bit_index(key))
        if not self._words[word] & mask:
            return False
        self._words[word] &= ~mask
        self._count -= 1
        return True

    def to_list(self):
        """Return the keys in increasing order for testing and iteration."""
        return [key for key in range(self._universe_size) if self.contains(key)]

failures = 0


def check(actual, expected, label):
    global failures
    if actual == expected:
        print(f"pass: {label}")
    else:
        failures += 1
        print(f"FAIL: {label} (expected {expected!r}, got {actual!r})")


def check_raises(action, label):
    global failures
    try:
        action()
        failures += 1
        print(f"FAIL: {label} (no exception raised)")
    except IndexError:
        print(f"pass: {label}")


def check_invalid_universe(label):
    global failures
    try:
        BitVectorIntSet(0)
        failures += 1
        print(f"FAIL: {label} (no exception raised)")
    except ValueError:
        print(f"pass: {label}")


def test_set():
    values = BitVectorIntSet(128)
    check(values.universe_size(), 128, "constructor records universe size")
    check(values.is_empty(), True, "new set is empty")
    check(values.size(), 0, "new set has size zero")
    check(values.contains(63), False, "valid missing key is absent")
    check(values.remove(96), False, "removing a missing key changes nothing")

    across_all_words = [0, 31, 32, 47, 63, 64, 95, 96, 127]
    for key in across_all_words:
        check(values.add(key), True, f"add key {key}")
    check(values.size(), len(across_all_words),
          "size includes keys stored in all four words")
    check(values.to_list(), across_all_words,
          "iteration finds boundary keys in increasing order")
    check(values.contains(31), True, "find high bit of first word")
    check(values.contains(32), True, "find low bit of second word")
    check(values.contains(64), True, "find low bit of third word")
    check(values.contains(127), True, "find high bit of fourth word")
    check(values.contains(30), False, "nearby clear bit remains absent")

    check(values.add(64), False, "reject duplicate key")
    check(values.size(), len(across_all_words), "duplicate does not change size")
    check(values.remove(31), True, "remove high bit of first word")
    check(values.remove(64), True, "remove low bit of third word")
    check(values.remove(127), True, "remove high bit of fourth word")
    check(values.remove(64), False, "cannot remove a key twice")
    check(values.to_list(), [0, 32, 47, 63, 95, 96],
          "removal clears only the selected bits")

    check_raises(lambda: values.contains(-1), "reject negative key")
    check_raises(lambda: values.add(128), "reject key equal to universe size")

    values.clear()
    check(values.is_empty(), True, "clear empties the set")
    check(values.size(), 0, "size is zero after clear")
    check(values.contains(0), False, "clear resets the first word")
    check(values.contains(96), False, "clear resets the final word")
    check(values.add(127), True, "set can be reused after clear")


def test_every_bit_around_word_boundaries():
    for universe in (1, 31, 32, 33, 63, 64, 65, 257):
        values = BitVectorIntSet(universe)
        for key in range(universe):
            check(values.add(key), True, f"add key {key} in universe {universe}")
        check(values.size(), universe, f"all keys counted in universe {universe}")
        for key in range(universe):
            check(values.contains(key), True, f"find key {key} in universe {universe}")
        for key in range(0, universe, 2):
            check(values.remove(key), True, f"remove even key {key}")
        for key in range(universe):
            check(values.contains(key), key % 2 == 1, f"post-removal membership for {key}")
        values.clear()
        check(values.is_empty(), True, f"clear universe {universe}")


def test_million_bit_universe():
    universe = 1_000_000
    values = BitVectorIntSet(universe)
    ok = all(values.add(key) for key in range(universe))
    ok = (values.size() == universe and values.contains(0) and values.contains(31)
          and values.contains(32) and values.contains(999_999) and ok)
    ok = all(values.remove(key) for key in range(0, universe, 2)) and ok
    ok = (values.size() == universe // 2 and not values.contains(0)
          and values.contains(999_999) and not values.contains(999_998) and ok)
    values.clear()
    check(ok and values.is_empty() and values.size() == 0, True, "million-bit aggregate workload")


if __name__ == "__main__":
    test_set()
    test_every_bit_around_word_boundaries()
    test_million_bit_universe()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
