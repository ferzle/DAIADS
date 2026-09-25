import inspect


class IntList:
    def __init__(self, capacity):
        self.A = [0] * capacity
        self.count = 0

    def is_empty(self):
        return self.count == 0

    def size(self):
        return self.count

    def clear(self):
        self.count = 0

    def first(self):
        return -1 if self.is_empty() else self.A[0]

    def last(self):
        return -1 if self.is_empty() else self.A[self.count - 1]

    def get(self, index):
        return -1 if index < 0 or index >= self.count else self.A[index]

    def set(self, index, value):
        if index < 0 or index >= self.count:
            return False
        self.A[index] = value
        return True

    def add_first(self, value):
        return self.insert(0, value)

    def add_last(self, value):
        return self.insert(self.count, value)

    def insert(self, index, value):
        if index < 0 or index > self.count or self.count == len(self.A):
            return False
        for i in range(self.count, index, -1):
            self.A[i] = self.A[i - 1]
        self.A[index] = value
        self.count += 1
        return True

    def remove_first(self):
        return self.remove(0)

    def remove_last(self):
        return self.remove(self.count - 1)

    def remove(self, index):
        if index < 0 or index >= self.count:
            return -1
        value = self.A[index]
        for i in range(index, self.count - 1):
            self.A[i] = self.A[i + 1]
        self.count -= 1
        return value

    def index_of(self, value):
        for i in range(self.count):
            if self.A[i] == value:
                return i
        return -1

    def contains(self, value):
        return self.index_of(value) != -1

    def delete(self, value):
        index = self.index_of(value)
        if index == -1:
            return False
        self.remove(index)
        return True

    def used_values_for_testing(self):
        return self.A[:self.count]


failures = 0


def check(actual, expected):
    global failures
    line = inspect.currentframe().f_back.f_lineno
    if actual == expected:
        print(f"PASS at test line {line}: got {actual!r}")
    else:
        failures += 1
        print(f"FAIL at test line {line}: expected {expected!r} but got {actual!r}")


def test_list():
    values = IntList(5)

    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.first(), -1)
    check(values.last(), -1)
    check(values.get(0), -1)
    check(values.remove_first(), -1)
    check(values.remove_last(), -1)
    check(values.remove(0), -1)

    check(values.add_last(4), True)       # [4]
    check(values.add_last(7), True)       # [4, 7]
    check(values.add_first(2), True)      # [2, 4, 7]
    check(values.insert(2, 9), True)      # [2, 4, 9, 7]
    check(values.used_values_for_testing(), [2, 4, 9, 7])

    check(values.size(), 4)
    check(values.is_empty(), False)
    check(values.first(), 2)
    check(values.last(), 7)
    check(values.get(0), 2)
    check(values.get(2), 9)
    check(values.get(4), -1)
    check(values.get(-1), -1)
    check(values.set(-1, 8), False)
    check(values.insert(-1, 8), False)
    check(values.remove(-1), -1)

    check(values.set(1, 5), True)         # [2, 5, 9, 7]
    check(values.get(1), 5)
    check(values.set(4, 8), False)
    check(values.size(), 4)

    check(values.add_last(11), True)      # [2, 5, 9, 7, 11]
    check(values.size(), 5)
    check(values.add_last(13), False)     # full
    check(values.add_first(13), False)    # full
    check(values.insert(2, 13), False)    # full
    check(values.used_values_for_testing(), [2, 5, 9, 7, 11])
    check(values.size(), 5)

    check(values.index_of(9), 2)
    check(values.index_of(100), -1)
    check(values.contains(7), True)
    check(values.contains(100), False)

    check(values.remove(2), 9)            # [2, 5, 7, 11]
    check(values.get(2), 7)
    check(values.remove_first(), 2)       # [5, 7, 11]
    check(values.remove_last(), 11)       # [5, 7]
    check(values.size(), 2)
    check(values.first(), 5)
    check(values.last(), 7)

    check(values.delete(5), True)         # [7]
    check(values.delete(5), False)
    check(values.size(), 1)
    check(values.first(), 7)
    check(values.last(), 7)

    check(values.remove_last(), 7)        # []
    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.remove_last(), -1)

    check(values.add_last(6), True)       # [6]
    check(values.add_last(8), True)       # [6, 8]
    values.clear()                        # []
    check(values.is_empty(), True)
    check(values.size(), 0)
    check(values.first(), -1)

    duplicates = IntList(8)
    check(duplicates.add_last(5), True)
    check(duplicates.add_last(2), True)
    check(duplicates.add_last(5), True)
    check(duplicates.add_last(5), True)
    check(duplicates.index_of(5), 0)
    check(duplicates.delete(5), True)
    check(duplicates.size(), 3)
    check(duplicates.index_of(5), 1)

    large = IntList(256)
    large_ok = all(large.add_last(i) for i in range(256))
    large_ok = all(large.get(i) == i for i in range(256)) and large_ok
    large_ok = all(large.remove_first() == i for i in range(256)) and large_ok
    check(large_ok and large.is_empty(), True)


def test_large_indexed_workload():
    values = IntList(256)
    for i in range(200):
        check(values.add_last(i % 17), True)
    check(values.size(), 200)
    check(values.first(), 0)
    check(values.last(), 12)
    check(values.insert(100, 999), True)
    check(values.get(100), 999)
    check(values.size(), 201)
    check(values.remove(100), 999)
    check(values.size(), 200)
    check(values.index_of(5), 5)
    check(values.delete(5), True)
    check(values.index_of(5), 21)
    check(values.set(0, 777), True)
    check(values.get(0), 777)
    check(values.insert(-1, 8), False)
    check(values.insert(values.size() + 1, 8), False)
    check(values.size(), 199)
    for _ in range(199):
        values.remove_last()
    check(values.size(), 0)
    check(values.add_first(42), True)
    check(values.remove_first(), 42)


def test_hundred_thousand_appends_and_reads():
    count = 100_000
    values = IntList(count)
    ok = all(values.add_last(i) for i in range(count))
    ok = all(values.get(i) == i for i in range(count)) and ok
    check(ok and values.size() == count and values.first() == 0 and values.last() == count - 1, True)


test_list()
test_large_indexed_workload()
test_hundred_thousand_appends_and_reads()

if failures:
    raise SystemExit(1)
