from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class Entry:
    value: str
    priority_key: int
    sequence_number: int


class UnsortedArrayMinPriorityQueue:
    def __init__(self, initial_capacity: int):
        if initial_capacity < 1:
            raise ValueError("initial_capacity must be positive")
        self._entries: list[Optional[Entry]] = [None] * initial_capacity
        self._count = 0
        self._next_sequence_number = 0

    def is_empty(self) -> bool:
        """Return whether the used portion of the array is empty."""
        return self._count == 0

    def size(self) -> int:
        """Return the number of stored entries, not the array capacity."""
        return self._count

    def insert(self, value: str, priority_key: int) -> None:
        """Append a new entry to the used portion of the array."""
        new_entry = Entry(value, priority_key, self._next_sequence_number)

        self._ensure_capacity()
        self._entries[self._count] = new_entry
        self._count += 1
        self._next_sequence_number += 1

    def peek(self) -> Optional[Entry]:
        """Return the best entry without removing it, or None when empty."""
        return None if self.is_empty() else self._entries[self._find_best_index()]

    def extract(self) -> Optional[Entry]:
        """Remove and return the best entry, or None when empty."""
        if self.is_empty():
            return None
        best_index = self._find_best_index()
        best = self._entries[best_index]
        self._count -= 1
        self._entries[best_index] = self._entries[self._count]
        self._entries[self._count] = None
        return best

    def _find_best_index(self) -> int:
        """Return the best entry's index; call only when nonempty."""
        best = 0
        for i in range(1, self._count):
            first = self._entries[i]
            second = self._entries[best]
            assert first is not None and second is not None
            if self._is_better(first, second):
                best = i
        return best

    @staticmethod
    def _is_better(first: Entry, second: Entry) -> bool:
        """Return whether first should be extracted before second."""
        return (first.priority_key < second.priority_key
                or (first.priority_key == second.priority_key
                    and first.sequence_number < second.sequence_number))

    def _ensure_capacity(self) -> None:
        """Double the backing array's capacity when it is full."""
        if self._count == len(self._entries):
            self._entries.extend([None] * len(self._entries))

    def entries_for_testing(self) -> list[str]:
        return [f"{entry.value}:{entry.priority_key}#{entry.sequence_number}"
                for entry in self._entries[:self._count] if entry is not None]


failures = 0


def check(actual, expected, label: str) -> None:
    global failures
    if actual == expected:
        print("pass:", label)
    else:
        failures += 1
        print(f"FAIL: {label} (expected {expected!r}, got {actual!r})")


def check_entry(
    actual: Optional[Entry], expected_value: str, expected_key: int, label: str
) -> None:
    global failures
    if (
        actual is not None
        and actual.value == expected_value
        and actual.priority_key == expected_key
    ):
        print("pass:", label)
    else:
        failures += 1
        print(
            f"FAIL: {label} (expected {expected_value}:{expected_key}, "
            f"got {actual!r})"
        )


def check_empty(actual: Optional[Entry], label: str) -> None:
    check(actual, None, label)


def test_gap_filling_and_stability() -> None:
    queue = UnsortedArrayMinPriorityQueue(2)

    check(queue.is_empty(), True, "new queue is empty")
    check(queue.size(), 0, "new queue has size zero")
    check(queue.peek(), None, "peek on empty queue")
    check(queue.extract(), None, "extract on empty queue")

    queue.insert("A", 5)
    queue.insert("B", 1)
    queue.insert("C", 4)
    queue.insert("D", 3)
    queue.insert("E", 1)
    check(queue.entries_for_testing(),
          ["A:5#0", "B:1#1", "C:4#2", "D:3#3", "E:1#4"],
          "insertions append entries with increasing sequence numbers")

    check(queue.size(), 5, "resizing preserves all entries")
    check_entry(queue.peek(), "B", 1, "peek returns earliest best entry")
    check(queue.size(), 5, "peek does not remove an entry")

    # B is not last, so extracting it must fill an interior gap.
    check_entry(queue.extract(), "B", 1, "interior-gap extraction")
    check(queue.entries_for_testing(),
          ["A:5#0", "E:1#4", "C:4#2", "D:3#3"],
          "last entry fills the extracted interior gap")
    check_entry(queue.extract(), "E", 1, "stable tied-key extraction")
    check_entry(queue.extract(), "D", 3, "replacement remains searchable")
    check_entry(queue.extract(), "C", 4, "next extraction")
    check_entry(queue.extract(), "A", 5, "worst entry is extracted last")

    check(queue.is_empty(), True, "queue is empty after all extractions")
    check(queue.size(), 0, "size returns to zero")


def test_best_entry_already_last() -> None:
    queue = UnsortedArrayMinPriorityQueue(3)
    queue.insert("X", 8)
    queue.insert("Y", 6)
    queue.insert("Z", 2)

    check_entry(queue.extract(), "Z", 2, "best entry already last")
    check(queue.size(), 2, "last-entry extraction decreases size once")
    check_entry(queue.extract(), "Y", 6, "remaining entries stay valid")


def test_large_stable_drain():
    queue = UnsortedArrayMinPriorityQueue(1)
    for i in range(500): queue.insert(f"v{i}", i % 17)
    check(queue.size(), 500, "size after large insertion")
    for priority in range(17):
        for i in range(priority, 500, 17):
            check_entry(queue.extract(), f"v{i}", priority, f"stable large extraction {i}")
    check(queue.is_empty(), True, "empty after large drain")
    check_empty(queue.extract(), "extract after large drain")


if __name__ == "__main__":
    test_gap_filling_and_stability()
    test_best_entry_already_last()
    test_large_stable_drain()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")

if __name__ == "__main__" and failures:
    raise SystemExit(1)
