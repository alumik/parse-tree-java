# LR(1) Parse Tree Generator

![JDK-15](https://img.shields.io/badge/JDK-15-blue)
![version-1.0.2](https://img.shields.io/badge/version-1.0.2-blue)
[![license-MIT](https://img.shields.io/badge/license-MIT-green)](https://github.com/AlumiK/parse-tree/blob/main/LICENSE)

This project is heavily inspired by [LYRON](https://github.com/LLyronx/LYRON), a universal compiler framework.

## Usage

Modify `config.yml` if necessary, place your test file, e.g., `test.txt`, and then execute

```
cat test.txt | java -jar parse-tree.jar
```

You can also execute the jar file directly and input your test code in the console.

## Examples

### Lexical Analysis

#### NFA to DFA

Take the regular expression `(a|b)*abb` for example.

1. Create an NFA.

    ![1-NFA](https://raw.githubusercontent.com/AlumiK/images/main/parse-tree/1_nfa.png)
    
2. Convert the NFA to a DFA.

    ![1-DFA](https://raw.githubusercontent.com/AlumiK/images/main/parse-tree/1_dfa.png)

#### Merge multiple NFAs

Take the regular expressions `a*b+`, `a`, `abb` for example.

1. Create three NFAs and combine them into one large NFA.

    ![2-NFA](https://raw.githubusercontent.com/AlumiK/images/main/parse-tree/2_nfa.png)

2. Convert the DFA to DFA.

    ![2-DFA](https://raw.githubusercontent.com/AlumiK/images/main/parse-tree/2_dfa.png)

### Creating Parse Tree

1. Get the predefined grammar.

    ```
    1. S'->S
    2. S->CβBA
    3. A->Aαβ
    4. A->αβ
    5. B->C
    6. B->Dβ
    7. C->α
    8. D->α
    ```

2. Write a config file (in YAML format).

    ```yaml
    nonterminalSymbols:
      # ? name
      ? A
      ? B
      ? C
      ? D
      ? S
    terminalSymbols:
      # name: regex
      α: a
      β: b
    ignoredSymbols:
    startSymbol: S
    productions:
      # - left part -> right part
      - S -> C β B A
      - A -> A α β
      - A -> α β
      - B -> C
      - B -> D β
      - C -> α
      - D -> α
    ```

3. Generate the parse table.

    <table>
      <tr>
        <th></th>
        <th colspan="3">ACTION</th>
        <th colspan="5">GOTO</th>
      </tr>
      <tr>
        <td></td>
        <td>α</td>
        <td>β</td>
        <td>$</td>
        <td>S</td>
        <td>A</td>
        <td>B</td>
        <td>C</td>
        <td>D</td>
      </tr>
      <tr>
        <td>0</td>
        <td>s1</td>
        <td></td>
        <td></td>
        <td>3</td>
        <td></td>
        <td></td>
        <td>2</td>
        <td></td>
      </tr>
      <tr>
        <td>1</td>
        <td></td>
        <td>r7</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>2</td>
        <td></td>
        <td>s4</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>3</td>
        <td></td>
        <td></td>
        <td>acc</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>4</td>
        <td>s5</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td>6</td>
        <td>7</td>
        <td>8</td>
      </tr>
      <tr>
        <td>5</td>
        <td>r7</td>
        <td>r8</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>6</td>
        <td>s10</td>
        <td></td>
        <td></td>
        <td></td>
        <td>9</td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>7</td>
        <td>r5</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>8</td>
        <td></td>
        <td>s11</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>9</td>
        <td>s12</td>
        <td></td>
        <td>r2</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>10</td>
        <td></td>
        <td>s13</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>11</td>
        <td>r6</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>12</td>
        <td></td>
        <td>s14</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>13</td>
        <td>r4</td>
        <td></td>
        <td>r4</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
      <tr>
        <td>14</td>
        <td>r3</td>
        <td></td>
        <td>r3</td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
        <td></td>
      </tr>
    </table>

4. Generate the parse tree.

    The input string is `abababab`.
    
    ![Parse Tree](https://raw.githubusercontent.com/AlumiK/images/main/parse-tree/parse_tree.png)
