Day13IteratorPattern — Iterator (Library Books)
=================================================

Assignment
----------
  Aggregate: Library (stores books)
  Iterator: BookIterator — hasNext(), next()
  Concrete: LibraryForwardIterator (currentIndex)
  Bonus: LibraryReverseIterator (backward)

Goal
----
Client traverses books WITHOUT knowing array / list / queue.

What you did right
------------------
  - Library creates and returns iterators
  - Forward/backward concrete iterators with index cursor
  - Client loops with hasNext + advance

Learning fixes
--------------
  1. getBookInv() exposed internal List — breaks encapsulation. Use package-private
     bookAt()/bookCount() so only iterators access storage.

  2. Separate data() + next()/prev() is non-standard. Use next() to return the
     book AND move cursor (standard hasNext/next loop).

  3. Two interfaces (ForwardIterator, BackwardIterator) — bonus reverse can share
     BookIterator; same client loop for both directions.

  4. Typos: Librery -> Library, Interator -> Iterator.

  5. Typing: BookIterator forward = library.forwardIterator() (program to interface).

Compile & run (from LLD folder)
-------------------------------
  javac Day13IteratorPattern/*.java
  java Day13IteratorPattern.Main

Iterator vs Composite
---------------------
  Iterator: sequential access without exposing structure
  Composite: tree structure with uniform showDetails on parts and whole
