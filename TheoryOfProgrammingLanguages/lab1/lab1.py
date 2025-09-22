from dataclasses import dataclass
import re
from typing import List, Optional
import matplotlib.pyplot as plt
import math
import os

@dataclass
class Token:
    type: str
    value: str
    pos: str

@dataclass
class Node:
    type: str
    value: Optional[str] = None
    children: List['Node'] = None

    def __post_init__(self):
        if self.children is None:
            self.children = []


class Parser:
    def __init__(self, input:str):
        self.tokens = self.tokenize(input)
        self.current_token_index = 0
        self.current_token = self.tokens[0] if self.tokens else None
        self.errors = []

    def tokenize(self, input:str) -> List[Token]:
        token_spec = [
            ('Number',      r'\d+'),
            ('Id',          r'[a-zA-Z][a-zA-Z0-9]*'),
            ('Plus',        r'\+'),
            ('Minus',       r'-'),
            ('Mult',        r'\*'),
            ('Div',         r'/'),
            ('LBracket',    r'\('),
            ('RBracket',    r'\)'),
            ('Space',       r'\s+'),
            ('Unknown',     r'.')
        ]

        tokens = []
        pos = 0
        input = input.strip()

        while pos < len(input):
            match = None
            for token_type, pattern in token_spec:
                regex = re.compile(pattern)
                match = regex.match(input, pos)
                if match:
                    value = match.group(0)
                    if token_type != 'Space':
                        if token_type == 'Unknown':
                            self.errors.append(f'Неизвестный символ {value}, pos:{pos}')
                        else:
                            tokens.append(Token(token_type, value, pos))
                    pos = match.end()
                    break
            if not match:
                pos += 1
        
        tokens.append(Token('EOF', '', pos))
        return tokens
    
    def match(self, expected:str)->bool:
        if self.current_token and self.current_token.type == expected:
            self.get_next_token()
            return True
        else:
            expected_types = {
                'Number': 'number',
                'Id': 'id',
                'Plus': '+',
                'Minus': '-',
                'Mult': '*',
                'Div': '/',
                'LBracket': '(',
                'RBracket': ')'
            }
            expected_message = expected_types.get(expected, expected)
            current_value = f"'{self.current_token.value}'" if self.current_token else "EOF"
            self.errors.append(f'Ожидалось {expected_message}, но получено {current_value}')
            return False
    
    def get_next_token(self) -> Token:
        self.current_token_index += 1
        if self.current_token_index < len(self.tokens):
            self.current_token = self.tokens[self.current_token_index]
        else:
            self.current_token = Token('EOF', '', -1)
        return self.current_token
    
    def parse(self):
        try:
            res = self.parseS()
            if self.current_token.type != 'EOF':
                self.errors.append(f'Неожиданный токен в конце: {self.current_token.value}')
            return res if not self.errors else None
        except Exception as e:
            self.errors.append(f'Ошибка парсинга {str(e)}')
            return None

    def parseS(self) -> Node:
        node = Node('S')
        node.children.append(self.parseT())
        node.children.append(self.parseE())
        return node

    def parseE(self):
        node = Node('E')

        if self.current_token and self.current_token.type in ['Plus', 'Minus']:
            operator_node = Node('Operator', self.current_token.value)
            node.children.append(operator_node)

            if self.current_token.type == 'Plus':
                self.match('Plus')
            else:
                self.match('Minus')

            node.children.append(self.parseT())
            node.children.append(self.parseE())
        else:
            node.children.append(Node('ε'))
        
        return node

    def parseT(self):
        node = Node('T')
        node.children.append(self.parseF())
        node.children.append(self.parseTPrime())
        return node
    
    def parseTPrime(self):
        node = Node("T'")

        if self.current_token and self.current_token.type in ['Mult', 'Div']:
            operator_node = Node('Operator', self.current_token.value)
            node.children.append(operator_node)

            if self.current_token.type == 'Mult':
                self.match('Mult')
            else:
                self.match('Div')

            node.children.append(self.parseF())
            node.children.append(self.parseTPrime())
        else:
            node.children.append(Node('ε'))

        return node

    def parseF(self):
        node = Node('F')

        if self.current_token and self.current_token.type == 'LBracket':
            self.match('LBracket')
            node.children.append(self.parseS())
            if not self.match('RBracket'):
                raise Exception('Ожидалась )')
        
        elif self.current_token and self.current_token.type == 'Number':
            node.value = self.current_token.value
            self.match('Number')
        
        elif self.current_token and self.current_token.type == 'Id':
            node.value = self.current_token.value
            self.match('Id')

        else:
            expected = ['number', 'id', '(']
            raise Exception(f"Ожидалось: {', '.join(expected)}")
        
        return node
    
    def visualize_tree(self, node: Node, filename: str = 'syntax_tree.png', node_radius: float = 0.03):

        sizes = {}
        max_depth = 0

        def calc_sizes(n, depth=0):
            nonlocal max_depth
            if n is None:
                return 0
            max_depth = max(max_depth, depth)
            total = 0
            if n.children:
                for c in n.children:
                    total += calc_sizes(c, depth + 1)
            if total == 0:
                total = 1
            sizes[id(n)] = total
            return total

        total_leaves = calc_sizes(node)

        vert_gap = max(0.12, node_radius * 3.0)

        fig_width = max(8, total_leaves * 1.8)
        fig_height = max(6, (max_depth + 1) * 1.6)
        fig, ax = plt.subplots(figsize=(fig_width, fig_height))

        coords = {}

        def assign_coords(n, x0, x1, depth=0):
            if n is None:
                return
            x = (x0 + x1) / 2.0
            y = 1.0 - depth * vert_gap
            coords[id(n)] = (x, y)
            if n.children:
                cur = x0
                total = sizes.get(id(n), 1)
                for c in n.children:
                    csize = sizes.get(id(c), 1)
                    span = (csize / total) * (x1 - x0) if total > 0 else (x1 - x0) / max(1, len(n.children))
                    assign_coords(c, cur, cur + span, depth + 1)
                    cur += span

        assign_coords(node, 0.0, 1.0, 0)

        nodes_info = []  # (x,y,label,type)
        edges = []       # ((x1,y1),(x2,y2))

        def traverse(n):
            if n is None:
                return
            x, y = coords[id(n)]

            if n.type == 'ε':
                label = 'ε'
            elif n.type == 'Operator' and n.value:
                label = n.value
            else:
                label = n.type

            nodes_info.append((x, y, label, n.type))

            if n.value is not None and n.type in ['F', 'Id', 'Number']:
                val_x = x
                val_y = y - vert_gap * 0.6
                nodes_info.append((val_x, val_y, str(n.value), 'Value'))
                edges.append(((x, y), (val_x, val_y)))

            for c in n.children or []:
                child_coord = coords.get(id(c))
                if child_coord:
                    edges.append(((x, y), child_coord))
                traverse(c)

        traverse(node)

        for (start, end) in edges:
            (x1, y1), (x2, y2) = start, end
            dx, dy = x2 - x1, y2 - y1
            dist = math.hypot(dx, dy)
            if dist == 0:
                continue
            r = node_radius
            offset_x = dx / dist * r
            offset_y = dy / dist * r
            sx, sy = x1 + offset_x, y1 + offset_y
            ex, ey = x2 - offset_x, y2 - offset_y
            ax.plot([sx, ex], [sy, ey], color='black', linewidth=1.5, alpha=0.85, zorder=1)

        all_x, all_y = [], []
        for x, y, label, ntype in nodes_info:
            if ntype == 'ε' :
                facecolor = 'lightgray'
            elif ntype == 'Value':
                facecolor = 'mediumpurple'
            elif ntype == 'Operator':
                facecolor = 'lightcoral'
            else:
                facecolor = 'lightblue'

            circle = plt.Circle((x, y), node_radius, color=facecolor, ec='black', lw=1.3, zorder=2)
            ax.add_patch(circle)

            fontsize = max(15, int(node_radius * 260)) 
            ax.text(x, y, label, ha='center', va='center', fontsize=fontsize, fontweight='bold', zorder=3)

            all_x.append(x)
            all_y.append(y)

        if all_x and all_y:
            margin = node_radius * 3.5
            xmin, xmax = min(all_x) - margin, max(all_x) + margin
            ymin, ymax = min(all_y) - margin, max(all_y) + margin
            ax.set_xlim(xmin, xmax)
            ax.set_ylim(ymin, ymax)

        ax.axis('off')
        ax.set_title(f'', fontsize=max(12, int(node_radius * 350)), pad=16)

        plt.tight_layout()
        plt.savefig(filename, dpi=300, bbox_inches='tight')
        plt.close()
        print(f"Дерево сохранено в {filename}")        

def main():

    print("  :q  - выход")
    print("  :h  - справка")
    print("  :ex - примеры выражений")
    
    while True:
        try:
            input_str = input("\n> ").strip()
            
            if input_str.lower() == ':q':
                break
            elif input_str.lower() == ':h':
                print("Number — последовательность цифр — 123, 42, 0")
                print("Id — последовательность букв и цифр, начинающаяся с буквы — x, count, a1, variable_name")
                continue
            elif input_str.lower() == ':ex':
                print("\nПРИМЕРЫ КОРРЕКТНЫХ ВЫРАЖЕНИЙ:")
                print("  2 + 3 * 4")
                print("  a * (b - 10)")
                print("\nПРИМЕРЫ С ОШИБКАМИ:")
                print("  5 + + 3  → Ожидалось: number, id или '('")
                print("  (7 * 2   → Ожидалось: ')'")
                continue
            elif not input_str:
                continue
            
            print(f"\nАнализ выражения: '{input_str}'")
            parser = Parser(input_str)
            parsed = parser.parse()

            if parser.errors:
                print("ВЫРАЖЕНИЕ НЕКОРРЕКТНО")
                print("Ошибки:")
                for error in parser.errors:
                    print(f"  • {error}")
            else:
                print("ВЫРАЖЕНИЕ КОРРЕКТНО")
                img_path = 'tree.png'
                parser.visualize_tree(parsed, filename=img_path)
                try:
                    os.startfile(img_path)
                except:
                    print("Не удалось открыть изображение автоматически")
                    print(f"Файл сохранен по пути: {os.path.abspath(img_path)}")
        
        except KeyboardInterrupt:
            break

        except Exception as e:
            print(f"❌ Неожиданная ошибка: {e}")

if __name__ == '__main__':
    main()
