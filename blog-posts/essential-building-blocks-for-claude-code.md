Building blocks make Claude code easier to write, read, and maintain. This short guide covers variables, functions, conditionals, and loops with concise examples.

### 1. Variables
Variables store data so you can reuse it later. Declare them before use:

```javascript
let message = "Hello, Claude!";
```

### 2. Functions
Functions encapsulate reusable behavior and reduce repetition:

```javascript
function greet() {
  console.log(message);
}
```

Note: greet() can access message because message is declared in an outer scope.

### 3. Conditionals
Conditionals run code based on a condition (truthiness):

```javascript
if (message) {
  greet();
}
```

### 4. Loops
Loops repeat work. Use them to perform a task multiple times:

```javascript
for (let i = 0; i < 5; i++) {
  console.log(i);
}
```

### Putting it all together
A small program that greets users five times:

```javascript
let message = "Hello, Claude!";

function greet() {
  console.log(message);
}

for (let i = 0; i < 5; i++) {
  greet();
}
```

### Conclusion
Variables, functions, conditionals, and loops are the core building blocks you’ll use repeatedly. Practice combining them to build clearer, more maintainable Claude code.