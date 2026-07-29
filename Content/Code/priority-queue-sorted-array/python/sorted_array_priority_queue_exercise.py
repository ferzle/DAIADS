from dataclasses import dataclass
from typing import Optional


@dataclass(frozen=True)
class Entry:
    value: str
    priority_key: int


class SortedArrayMinPriorityQueue:
    def __init__(self, initial_capacity: int):
        if initial_capacity < 1:
            raise ValueError("initial_capacity must be positive")
        self._entries: list[Optional[Entry]] = [None] * initial_capacity
        self._count = 0

    def is_empty(self) -> bool:
        """Return whether the used portion of the array is empty."""
        # TODO
        return False

    def size(self) -> int:
        """Return the number of stored entries, not the array capacity."""
        # TODO
        return -1

    def insert(self, value: str, priority_key: int) -> None:
        """Insert an entry while keeping the used range worst-to-best."""
        new_entry = Entry(value, priority_key)

        # TODO:
        # 1. Grow the array if _count equals len(_entries).
        # 2. Find the insertion index that keeps _entries[0:_count]
        #    ordered from worst to best. Insert a new tied entry on the
        #    left/worse side of all existing entries with the same key.
        # 3. Shift the required suffix one position to the right.
        # 4. Store new_entry and increment _count.

    def peek(self) -> Optional[Entry]:
        """Return the best entry without removing it, or None when empty."""
        # TODO
        return None

    def extract(self) -> Optional[Entry]:
        """Remove and return the best entry, or None when empty."""
        # TODO: The best entry is at the used right end. Clear the vacated
        # array cell after decreasing _count.
        return None

    @staticmethod
    def _is_better(first: Entry, second: Entry) -> bool:
        """Return whether first should be extracted before second."""
        # TODO: Smaller priority keys are better. Equal keys compare as
        # tied; stable behavior comes from their physical insertion order.
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


def test_sorted_array_priority_queue() -> None:
    queue = SortedArrayMinPriorityQueue(2)

    check(queue.is_empty(), True, "new queue is empty")
    check(queue.size(), 0, "new queue has size zero")
    check(queue.peek(), None, "peek on empty queue")
    check(queue.extract(), None, "extract on empty queue")

    # These insertions require placement at the used beginning, middle,
    # and end, and they force the backing array to resize.
    queue.insert("A", 5)
    queue.insert("B", 2)
    queue.insert("C", 7)
    queue.insert("D", 4)
    queue.insert("E", 2)

    check(queue.size(), 5, "size after five insertions")
    check_entry(queue.peek(), "B", 2, "peek returns earliest best entry")
    check(queue.size(), 5, "peek does not remove an entry")

    check_entry(queue.extract(), "B", 2, "first extraction")
    check_entry(queue.extract(), "E", 2, "stable tied-key extraction")
    check_entry(queue.extract(), "D", 4, "middle-priority extraction")
    check_entry(queue.extract(), "A", 5, "next extraction")
    check_entry(queue.extract(), "C", 7, "worst entry is extracted last")

    check(queue.is_empty(), True, "queue is empty after all extractions")
    check(queue.size(), 0, "size returns to zero")
    check(queue.extract(), None, "extract remains safe when empty")


if __name__ == "__main__":
    test_sorted_array_priority_queue()
    print("All tests passed." if failures == 0 else f"{failures} test(s) failed.")
