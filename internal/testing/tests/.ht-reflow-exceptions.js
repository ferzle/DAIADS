/**
 * WCAG 1.4.10 permits parts of content that require a two-dimensional layout
 * for usage or meaning. Keep these exceptions component-specific: an entry
 * never exempts the surrounding page, controls, prose, or other components.
 *
 * Selectors are evaluated only in the named Content file. The reflow test
 * temporarily removes the selected component and checks whether any
 * non-exempt content still causes document-level horizontal scrolling.
 */
module.exports = {
  // Long mathematical notation remains horizontally scrollable as one
  // expression so operators, subscripts, and aligned terms retain meaning.
  'Algorithms/Brute Force/Polynomial Evaluation.html': [
    { selector: 'mjx-container', reason: 'Mathematical expression preserves operator and term relationships.' },
  ],
  'Algorithms/Dynamic Programming/0-1 Knapsack.html': [
    { selector: 'mjx-container', reason: 'Recurrence notation preserves operator, subscript, and case relationships.' },
  ],
  "Algorithms/Dynamic Programming/Floyd's.html": [
    { selector: 'mjx-container', reason: 'Recurrence notation preserves operator and subscript relationships.' },
  ],
  "Algorithms/Dynamic Programming/Warshall's.html": [
    { selector: 'mjx-container', reason: 'Recurrence notation preserves operator and subscript relationships.' },
  ],
  'Algorithms/Greedy/Fractional Knapsack.html': [
    { selector: 'mjx-container', reason: 'Mathematical expression preserves ratio and term relationships.' },
  ],
  "Algorithms/Transform-and-Conquer/Horner's Rule.html": [
    { selector: 'mjx-container', reason: 'Nested polynomial notation preserves grouping and term relationships.' },
  ],
  'Problems/Other/Fibonacci.html': [
    { selector: 'mjx-container', reason: 'Recurrence notation preserves operator and subscript relationships.' },
  ],

  // Graphs, trees, geometric diagrams, and timelines.
  'Algorithms/Decrease-and-Conquer/Hoare Partition.html': [
    { selector: '#partition-svg', reason: 'Partition-state diagram preserves positional relationships.' },
  ],
  'Algorithms/Decrease-and-Conquer/Topological Sort DRAFT.html': [
    { selector: 'svg', reason: 'Directed graph layout preserves vertex and edge relationships.' },
  ],
  'Algorithms/Exhaustive Search/Topological Sort (DFS) DRAFT.html': [
    { selector: 'svg', reason: 'Directed graph layout preserves vertex and edge relationships.' },
  ],
  'Demos/Data Structures/BST Rotation Demo.html': [
    { selector: '#treeSvg', reason: 'Interactive tree layout preserves parent-child relationships.' },
  ],
  'Demos/Data Structures/Binary Tree Traversal Demo.html': [
    { selector: '#treeSvg', reason: 'Interactive tree layout preserves parent-child relationships.' },
  ],
  'Demos/Data Structures/Heap/Build Heap Demo.html': [
    { selector: '#svg', reason: 'Heap tree preserves parent-child and array-index relationships.' },
  ],
  'Demos/Data Structures/Heap/ExtractMax Demo.html': [
    { selector: '#svg', reason: 'Heap tree preserves parent-child and array-index relationships.' },
  ],
  'Demos/Data Structures/Heap/Heap Representation Demo.html': [
    { selector: '#svg', reason: 'Heap tree preserves parent-child and array-index relationships.' },
  ],
  'Demos/Data Structures/Heap/Insert Demo.html': [
    { selector: '#svg', reason: 'Heap tree preserves parent-child and array-index relationships.' },
  ],
  'Demos/Data Structures/Heap/SiftDown Demo.html': [
    { selector: '#svg', reason: 'Heap tree preserves parent-child and array-index relationships.' },
  ],
  'Demos/Decrease-and-Conquer/Topological Sort Demo.html': [
    { selector: '.graph-panel, .table-panel', reason: 'Graph and vertex-state table form one two-dimensional visualization.' },
  ],
  'Demos/Divide-and-Conquer/QuickHull Mini.html': [
    { selector: '#cv', reason: 'Geometric point and hull layout is inherently two-dimensional.' },
  ],
  'Demos/Exhaustive Search/BFS Demo.html': [
    { selector: '.graph-panel, .table-panel', reason: 'Graph and traversal-state table form one two-dimensional visualization.' },
  ],
  'Demos/Exhaustive Search/DFS (Directed) Demo.html': [
    { selector: '.graph-panel, .table-panel', reason: 'Graph and traversal-state table form one two-dimensional visualization.' },
  ],
  'Demos/Exhaustive Search/DFS Demo.html': [
    { selector: '.graph-panel, .table-panel', reason: 'Graph and traversal-state table form one two-dimensional visualization.' },
  ],
  'Demos/Exhaustive Search/Topological Sort Demo.html': [
    { selector: '.graph-panel, .table-panel', reason: 'Graph and traversal-state table form one two-dimensional visualization.' },
  ],
  'Demos/Greedy/Interval Scheduling Demo.html': [
    { selector: '#timeAxis, #intervals', reason: 'Timeline positions encode interval start, finish, and overlap.' },
  ],
  'Demos/Greedy/Prims Algorithm Demo.html': [
    { selector: '.graph-col, .table-col', reason: 'Graph and Prim state table form one two-dimensional visualization.' },
  ],

  // Mathematical matrices and matrix-based demonstrations.
  'Algorithms/Brute Force/Matrix Multiplication.html': [
    { selector: 'mjx-container:has(mtable)', reason: 'Matrix notation requires aligned rows and columns.' },
  ],
  'Algorithms/Divide-and-Conquer/Matrix Multiplication.html': [
    { selector: 'mjx-container:has(mtable)', reason: 'Matrix notation requires aligned rows and columns.' },
  ],
  'Algorithms/Divide-and-Conquer/Strassen.html': [
    { selector: 'mjx-container:has(mtable)', reason: 'Matrix notation requires aligned rows and columns.' },
  ],
  'Demos/Brute Force/Matrix Multiplication Demo.html': [
    { selector: '.matrix-panel', reason: 'Matrix cells require aligned rows and columns.' },
  ],
  'Demos/Divide-and-Conquer/Matrix Multiplcation (Inplace) Demo.html': [
    { selector: '.matrix-panel', reason: 'Matrix cells require aligned rows and columns.' },
  ],
  'Demos/Divide-and-Conquer/Matrix Multiplication Demo.html': [
    { selector: '.matrix-panel', reason: 'Matrix cells require aligned rows and columns.' },
  ],
  'Demos/Divide-and-Conquer/Strassens Demo.html': [
    { selector: '.matrix-panel', reason: 'Matrix cells require aligned rows and columns.' },
  ],
  "Demos/Dynamic Programming/Floyd's Demo.html": [
    { selector: '.matrix-panel', reason: 'Distance matrix requires aligned rows and columns.' },
  ],
  "Demos/Dynamic Programming/Warshall's Demo.html": [
    { selector: '.matrix-panel', reason: 'Reachability matrix requires aligned rows and columns.' },
  ],
  'Demos/Transform-and-Conquer/Fibonacci Number (Matrix) Demo.html': [
    { selector: '#matrix-row, #step-table', reason: 'Matrix equation and step table require aligned rows and columns.' },
  ],
  'Problems/Foundational/Matrix Multiplication.html': [
    { selector: 'mjx-container:has(mtable)', reason: 'Matrix notation requires aligned rows and columns.' },
  ],
  'Problems/Graphs/Transitive Closure.html': [
    { selector: 'mjx-container:has(mtable)', reason: 'Adjacency matrices require aligned rows and columns.' },
  ],

  // Large data/state tables and aligned string visualizations.
  'Demos/Brute Force/String Matching Demo.html': [
    { selector: '#indexRow, #visualization, #pattern-row, #failure-rows', reason: 'Character positions must remain aligned across rows.' },
  ],
  'Demos/Space-Time Tradeoff/Boyer-Moore Demo.html': [
    { selector: '.tables-container, #indexRow, #textArray, #patternArray, #failureSnapshots', reason: 'Precomputation tables and character positions require alignment.' },
  ],
  'Demos/Space-Time Tradeoff/Horspool Demo.html': [
    { selector: '#shift-table-header, #shift-table, #indexRow, #visualization, #pattern-row, #failure-rows', reason: 'Shift table and character positions require alignment.' },
  ],
  'Demos/Transform-and-Conquer/Binary Exponentiation RTL Demo.html': [
    { selector: '#step-table', reason: 'Algorithm state table requires column alignment.' },
  ],
  'Problems/Problem List.html': [
    { selector: '.problems-table', reason: 'Multi-column comparison table requires row and column relationships.' },
  ],
  'Algorithms/Space-Time Tradeoff/Radix Sort.html': [
    { selector: '.example-table', reason: 'Multi-column worked-example table requires row and column relationships.' },
    { selector: 'mjx-container', reason: 'Mathematical expression preserves operator and term relationships.' },
  ],

  // Problem-page diagrams. The surrounding example box is intentionally not
  // exempt, so a fixed-width container will still fail the reflow check.
  'Problems/Geometry/Closest Pair.html': [
    { selector: '.example-box svg', reason: 'Geometric distances depend on two-dimensional point placement.' },
  ],
  'Problems/Geometry/Convex Hull.html': [
    { selector: '.example-box svg', reason: 'Hull membership depends on two-dimensional point placement.' },
  ],
  'Problems/Graphs/All-Pairs Shortest Path.html': [
    { selector: '.example-box svg', reason: 'Graph layout preserves vertex and edge relationships.' },
  ],
  'Problems/Graphs/Graph Traversal.html': [
    { selector: '.example-box svg', reason: 'Graph layout preserves vertex and edge relationships.' },
  ],
  'Problems/Graphs/Minimum Spanning Tree.html': [
    { selector: '.example-box svg', reason: 'Graph layout preserves vertex and edge relationships.' },
  ],
  'Problems/Graphs/Maximum Flow.html': [
    { selector: '.example-box svg', reason: 'Network layout preserves capacities and edge relationships.' },
  ],
  'Problems/Graphs/Spanning Tree.html': [
    { selector: '.example-box svg', reason: 'Graph layout preserves vertex and edge relationships.' },
  ],
  'Problems/Graphs/Single-Source Shortest Path.html': [
    { selector: '.example-box svg', reason: 'Graph layout preserves vertex and edge relationships.' },
  ],
  'Problems/Graphs/Topological Sort.html': [
    { selector: '.example-box svg', reason: 'Directed graph layout preserves ordering constraints.' },
  ],
  'Problems/Optimization/Interval Scheduling.html': [
    { selector: '.example-box svg', reason: 'Timeline positions encode interval start, finish, and overlap.' },
  ],
  'Problems/Other/Travelling Salesman.html': [
    { selector: '.example-box svg', reason: 'Graph layout preserves route and edge relationships.' },
  ],
};
