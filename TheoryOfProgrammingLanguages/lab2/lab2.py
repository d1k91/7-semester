from typing import Set, Dict, Tuple
from itertools import combinations
from tabulate import tabulate


class Automaton:
    def __init__(self, Q: Set[str], alphabet: Set[str], start_state: str, end_states: Set[str], 
                 transitions: Dict[Tuple[str,str], Set[str]]):
        self.Q = Q
        self.alphabet = alphabet
        self.transitions = transitions
        self.start_state = start_state
        self.end_states = end_states

    def __str__(self):
        res = []
        res.append(f"Множество состояний: {sorted(self.Q)}")
        res.append(f"Алфавит: {sorted(self.alphabet)}")
        res.append("Переходы")
        for (state, symbol), next_states in sorted(self.transitions.items()):
            res.append(f"{state} x {symbol} -> {next_states}")
        res.append(f"Начальное состояние: {self.start_state}")
        res.append(f"Множество конечных состояний: {self.end_states}")
        return "\n".join(res)
    
    def is_Deterministic(self) -> bool:
        for q in self.Q:
            for s in self.alphabet:
                key = (q, s)
                if key in self.transitions:
                    next = self.transitions[key]
                    if len(next) > 1:
                        return False
                    if len(next) == 0:
                        return False
                else:
                    return False
        return True
    
    def to_Deterministic(self) -> 'Automaton':
        if self.is_Deterministic():
            return self
        
        all_subsets = []
        for r in range(1, len(self.Q) + 1):
            for subset in combinations(sorted(self.Q), r):
                all_subsets.append(set(subset))
        
        print("Все возможные комбинации состояний:", all_subsets)

        dka_transitions = {}
        
        for subset in all_subsets:
            for symbol in self.alphabet:
                next_states = set()
                for state in subset:
                    key = (state, symbol)
                    if key in self.transitions:
                        next_states.update(self.transitions[key])
                
                if next_states:
                    subset_key = tuple(sorted(subset))
                    next_states_key = tuple(sorted(next_states))
                    dka_transitions[(subset_key, symbol)] = next_states_key
                    print(f"{subset} x {symbol} -> {next_states}")

        dka_start = tuple(sorted({self.start_state}))
        dka_final = set()
        
        for subset in all_subsets:
            if any(state in self.end_states for state in subset):
                dka_final.add(tuple(sorted(subset)))
        
        reachable_states = self._find_reachable_states(dka_start, dka_transitions)
        
        print(f"Достижимые состояния: {reachable_states}")
        print(f"Конечные состояния ДКА: {dka_final & reachable_states}")

        state_mapping = {}
        new_states = set()
        new_transitions = {}
        new_final_states = set()
        
        sorted_reachable_states = sorted(reachable_states, key=lambda x: (len(x), x))
        
        for i, state_tuple in enumerate(sorted_reachable_states):
            state_name = f"q{i}"
            state_mapping[state_tuple] = state_name
            new_states.add(state_name)
            
            if state_tuple in dka_final:
                new_final_states.add(state_name)
        
        for (state_tuple, symbol), next_state_tuple in dka_transitions.items():
            if state_tuple in reachable_states and next_state_tuple in reachable_states:
                from_state = state_mapping[state_tuple]
                to_state = state_mapping[next_state_tuple]
                new_transitions[(from_state, symbol)] = {to_state}
        
        new_start_state = state_mapping[dka_start]
        
        return Automaton(new_states, self.alphabet, new_start_state, new_final_states, new_transitions)
        
    def minimize(self) -> 'Automaton':
        if not self.is_Deterministic():
            dka = self.to_Deterministic()
            return dka.minimize()

        final_states = set(self.end_states)
        partition = [{s for s in self.Q if s in final_states}, {s for s in self.Q if s not in final_states}]
        partition = [p for p in partition if p]

        while True:
            changed = False
            for i, group in enumerate(partition):
                if len(group) <= 1:
                    continue
                new_partition = []
                processed = set()
                for state1 in group:
                    if state1 in processed:
                        continue
                    same_group = {state1}
                    processed.add(state1)
                    for state2 in group - {state1}:
                        equivalent = True
                        for symbol in self.alphabet:
                            next1 = self.transitions.get((state1, symbol), {state1})
                            next2 = self.transitions.get((state2, symbol), {state2})
                            target_group1 = next((p for p in partition if next(iter(next1)) in p), None)
                            target_group2 = next((p for p in partition if next(iter(next2)) in p), None)
                            if target_group1 != target_group2:
                                equivalent = False
                                break
                        if equivalent:
                            same_group.add(state2)
                            processed.add(state2)
                    new_partition.append(same_group)
                if len(new_partition) > 1:
                    changed = True
                    partition[i:i+1] = new_partition
                    break
            if not changed:
                break

        new_states = {f"q{i}" for i in range(len(partition))}
        state_mapping = {}
        for i, group in enumerate(partition):
            for state in group:
                state_mapping[state] = f"q{i}"
        new_transitions = {}
        new_start_state = state_mapping[self.start_state]
        new_final_states = {f"q{i}" for i, group in enumerate(partition) if any(state in final_states for state in group)}

        for from_state in self.Q:
            new_from = state_mapping[from_state]
            for symbol in self.alphabet:
                if (from_state, symbol) in self.transitions:
                    to_state = next(iter(self.transitions[(from_state, symbol)]))
                    new_to = state_mapping[to_state]
                    if (new_from, symbol) not in new_transitions:
                        new_transitions[(new_from, symbol)] = {new_to}
                    else:
                        new_transitions[(new_from, symbol)].add(new_to)

        return Automaton(new_states, self.alphabet, new_start_state, new_final_states, new_transitions)
    
    def _find_reachable_states(self, start_state: tuple, transitions: Dict) -> Set[tuple]:
        reachable = set()
        queue = [start_state]
        reachable.add(start_state)
        while queue:
            current_state = queue.pop(0)
            for symbol in self.alphabet:
                key = (current_state, symbol)
                if key in transitions:
                    next_state = transitions[key]
                    if next_state not in reachable:
                        reachable.add(next_state)
                        queue.append(next_state)
        return reachable

    
def input_automaton() -> Automaton:
    while True:
        states_input = input("Введите состояния (через пробел): ").strip()
        if states_input:
            states = set(states_input.split())
            break
        print("Ошибка: состояния не могут быть пустыми")
    
    while True:
        alphabet_input = input("Введите алфавит (символы через пробел): ").strip()
        if alphabet_input:
            alphabet = set(alphabet_input.split())
            break
        print("Ошибка: алфавит не может быть пустым")
    
    while True:
        initial_state = input("Введите начальное состояние: ").strip()
        if initial_state in states:
            break
        print(f"Ошибка: начальное состояние должно быть одним из {states}")
    
    while True:
        final_input = input("Введите финальные состояния (через пробел): ").strip()
        if final_input:
            final_states = set(final_input.split())
            if final_states.issubset(states):
                break
            print(f"Ошибка: финальные состояния должны быть подмножеством {states}")
        else:
            print("Ошибка: финальные состояния не могут быть пустыми")
    
    transitions = {}
    print("\nВведите переходы (для завершения введите 'end'):")
    print("Формат: <исходное_состояние> <символ> <целевые_состояния_через_пробел>")
    print("Пример: q0 a q1 q2")
    
    while True:
        transition_input = input("Переход: ").strip()
        if transition_input.lower() == 'end':
            break
        
        if not transition_input:
            continue
            
        parts = transition_input.split()
        if len(parts) < 3:
            print("Ошибка: неверный формат. Нужно: состояние символ целевые_состояния")
            continue
        
        from_state = parts[0]
        symbol = parts[1]
        to_states = set(parts[2:])
        
        if from_state not in states:
            print(f"Ошибка: состояние {from_state} не существует")
            continue
        
        if symbol not in alphabet and symbol not in ['ε', '']:
            print(f"Ошибка: символ {symbol} не в алфавите")
            continue
        
        for to_state in to_states:
            if to_state not in states:
                print(f"Ошибка: состояние {to_state} не существует")
                break
        else:
            key = (from_state, symbol)
            if key in transitions:
                transitions[key].update(to_states)
            else:
                transitions[key] = to_states
            print(f"{from_state} x {symbol} -> {to_states}")
    
    return Automaton(states, alphabet, initial_state, final_states, transitions)

def print_transition_table(automaton):
    alphabet = sorted(automaton.alphabet)
    states = sorted(automaton.Q)
    headers = ["Состояние"] + alphabet
    table_data = []
    for state in states:
        row = [state]
        for symbol in alphabet:
            next_states = automaton.transitions.get((state, symbol), set())
            if not next_states:
                row.append("-")
            else:
                row.append(", ".join(sorted(next_states)))
        table_data.append(row)
    print(tabulate(table_data, headers=headers, tablefmt="grid"))



def main():
    a = input_automaton()
    print(a)
    print_transition_table(a)
    b = a.to_Deterministic()
    c = b.minimize()
    print(c)
    print_transition_table(c)    


if __name__ == "__main__":
    main()