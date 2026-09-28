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
        # TODO
        return False

    def size(self) -> int:
        """Return the number of stored entries, not the array capacity."""
        # TODO
        return -1

    def insert(self, value: str, priority_key: int) -> None:
        """Append a new entry to the used portion of the array."""
        new_entry = Entry(value, priority_key, self._next_sequence_number)

        # TODO:
        # 1. Grow the array if _count equals len(_entries).
        # 2. Append new_entry at _entries[_count].
        # 3. Increment _count and advance the sequence number.

    def peek(self) -> Optional[Entry]:
        """Return the best entry without removing it, or None when empty."""
        # TODO: Find the best index and return that entry.
        return None

    def extract(self) -> Optional[Entry]:
        """Remove and return the best entry, or None when empty."""
        # TODO:
        # 1. Find and save the best entry.
        # 2. Move the final used entry into the removed entry's gap.
        # 3. Decrement _count and clear the old final array cell.
        # The same steps must work when the best entry is already last.
        return None

    def _find_best_index(self) -> int:
        """Return the best entry's index; call only when nonempty."""
        # TODO: Scan _entries[0:_count] and use _is_better.
        return -1

    @staticmethod
    def _is_better(first: Entry, second: Entry) -> bool:
        """Return whether first should be extracted before second."""
        # TODO: Smaller priority keys are better. If keys tie, the entry
        # with the smaller sequence number is better.
        return False

    def _ensure_capacity(self) -> None:
        """Double the backing array's capacity when it is full."""
        # TODO


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

    check(queue.size(), 5, "resizing preserves all entries")
    check_entry(queue.peek(), "B", 1, "peek returns earliest best entry")
    check(queue.size(), 5, "peek does not remove an entry")

    # B is not last, so extracting it must fill an interior gap.
    check_entry(queue.extract(), "B", 1, "interior-gap extraction")
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


if __name__ == "__main__":
    test_gap_filling_and_stability()
    test_best_entry_already_last()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
