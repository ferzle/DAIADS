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
        # TODO: Return the index of the 32-bit word that stores key.
        return -1

    def _bit_index(self, key):
        # TODO: Return key's bit position within its word (0 through 31).
        return -1

    def _word_mask(self, bit_index):
        # TODO: Return a 32-bit word with only bit_index set to 1.
        # bit_index must be between 0 and 31. Python ints can grow beyond
        # 32 bits, but each list entry models exactly one 32-bit word.
        return 0

    def _check_key(self, key):
        if key < 0 or key >= self._universe_size:
            raise IndexError(f"key must be in [0, {self._universe_size})")

    def universe_size(self):
        return self._universe_size

    def is_empty(self):
        # TODO: Return whether the set contains no keys.
        return False

    def size(self):
        # TODO: Return the number of distinct keys currently present.
        return -1

    def clear(self):
        # TODO: Clear every word and reset the count.
        pass

    def contains(self, key):
        self._check_key(key)
        # TODO: Use _word_index(key), _bit_index(key), and
        # _word_mask(bit_index) to find and test key's membership bit.
        return False

    def add(self, key):
        self._check_key(key)
        # TODO: Use the three helpers above. If the selected bit is already 1,
        # return False. Otherwise, set it with bitwise OR, increment the count
        # once, and return True.
        return False

    def remove(self, key):
        self._check_key(key)
        # TODO: Use the three helpers above. If the selected bit is already 0,
        # return False. Otherwise, clear it with bitwise AND and complement,
        # decrement the count once, and return True.
        return False

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
    check_invalid_universe("reject nonpositive universe size")
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


if __name__ == "__main__":
    test_set()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
