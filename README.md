# LR(1) Parse Tree Generator

This project is heavily inspired by [LYRON](https://github.com/LLyronx/LYRON), a universal compiler framework.

## Examples

### Lexical Analysis

#### NFA to DFA

Take the regular expression `(a|b)*abb` for example.

1. Create an NFA.

    ![KPTaGR.png](https://s2.ax1x.com/2019/10/16/KPTaGR.png)
    
2. Convert the NFA to a DFA.

    ![KPTtIJ.png](https://s2.ax1x.com/2019/10/16/KPTtIJ.png)

#### Merge multiple NFAs

Take the regular expressions `a*b+`, `a`, `abb` for example.

1. Create three NFAs and combine them into one large NFA.

    ![KPTYa4.png](https://s2.ax1x.com/2019/10/16/KPTYa4.png)

2. Convert the DFA to DFA.

    ![KPTUi9.png](https://s2.ax1x.com/2019/10/16/KPTUi9.png)

### Creating Parse Tree

1. Get the predefined grammar.

    ```
    1. S'->S
    2. S->CbBA
    3. A->Aab
    4. A->ab
    5. B->C
    6. B->Db
    7. C->a
    8. D->a
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
      a: a
      b: b
    ignoredSymbols:
    startSymbol: S
    productions:
      # - left part -> right part
      - S -> C b B A
      - A -> A a b
      - A -> a b
      - B -> C
      - B -> D b
      - C -> a
      - D -> a
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
        <td>a</td>
        <td>b</td>
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
    
    ![KC59Yt.png](https://s2.ax1x.com/2019/10/15/KC59Yt.png)
