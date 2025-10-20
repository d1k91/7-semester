from primeFerma import *
from Diffie_Hellman import *
import random

def vernam(data, p, g, mode='encrypt', keys_used=None):
    result = bytearray()
    
    if mode == 'encrypt':
        new_keys_used = []
        for i in range(len(data)):
            x_a = random.randint(1, p - 1)
            x_b = random.randint(1, p - 1)
            new_keys_used.append((x_a, x_b))
            
            block_key = Diffie_Hellman(p, g, x_a, x_b)
            key_byte = block_key & 0xFF
            result.append(data[i] ^ key_byte)
        
        return bytes(result), new_keys_used
    else:
        for i in range(len(data)):
            x_a, x_b = keys_used[i]
            block_key = Diffie_Hellman(p, g, x_a, x_b)
            key_byte = block_key & 0xFF
            result.append(data[i] ^ key_byte)
        
        return bytes(result), None

def vernam_encrypt_file(input_file, output_file, p, g):
    with open(input_file, "rb") as f:
        data = f.read()
    
    encrypted, keys_used = vernam(data, p, g, 'encrypt')
    
    with open(output_file, "wb") as f:
        f.write(encrypted)
    
    return keys_used

def vernam_decrypt_file(input_file, output_file, p, g, keys_used):
    with open(input_file, "rb") as f:
        encrypted_data = f.read()
    
    decrypted, _ = vernam(encrypted_data, p, g, 'decrypt', keys_used)
    
    with open(output_file, "wb") as f:
        f.write(decrypted)

def save_keys(keys_used, key_file_path, p, g):
    with open(key_file_path, "w") as f:
        f.write(f"{p}\n{g}\n")
        f.write(f"{len(keys_used)}\n")
        for x_a, x_b in keys_used:
            f.write(f"{x_a} {x_b}\n")

def load_keys(key_file_path):
    with open(key_file_path, "r") as f:
        p = int(f.readline().strip())
        g = int(f.readline().strip())
        count = int(f.readline().strip())
        keys_used = []
        for _ in range(count):
            line = f.readline().strip()
            x_a, x_b = map(int, line.split())
            keys_used.append((x_a, x_b))
    
    return p, g, keys_used

def main(): 
    while True:
        print("\n1. Шифрование файла")
        print("2. Дешифрование файла")
        print("3. Выход")
        
        choice = input("Выберите действие: ")
        
        if choice == "1":
            key_source = input("1. Ввести параметры Диффи-Хеллмана вручную\n2. Сгенерировать автоматически\n")
            if key_source == "1":
                p = int(input("Введите p: "))
                while not isPrimeFerma(p):
                    p = int(input("P должно быть простым!\nВведите другое p: "))
                
                q = (p - 1) // 2
                g = int(input("Введите g: "))
                while modular_exponentiation(g, q, p) == 1:
                    g = int(input("g^q mod p = 1!\nВведите другое g: "))
                
            else:
                print("Генерация параметров Диффи-Хеллмана...")
                p, g, x_a, x_b = generate_dh_parameters()
                print(f"p = {p}")
                print(f"g = {g}")
            
            input_file = input("Введите путь к файлу для шифрования: ")
            output_file = input("Введите путь для зашифрованного файла: ")
            
            try:
                keys_used = vernam_encrypt_file(input_file, output_file, p, g)
                print("Файл успешно зашифрован!")
                
                save_keys(keys_used, output_file + ".key", p, g)
                print(f"Все параметры и ключи сохранены в {output_file}.key")
                print(f"Размер файла ключей: {len(keys_used)} пар ключей")
                
            except Exception as e:
                print(f"Ошибка при шифровании: {e}")
        
        elif choice == "2":
            decrypt_method = input("1. Использовать сохраненные параметры ключа\n2. Ввести параметры вручную\n")
            
            if decrypt_method == "1":
                key_file_path = input("Введите путь к файлу с параметрами ключа: ")
                try:
                    p, g, keys_used = load_keys(key_file_path)
                    print(f"Параметры загружены: p={p}, g={g}")
                    print(f"Загружено {len(keys_used)} пар ключей")
                    
                except Exception as e:
                    print(f"Ошибка при загрузке параметров ключа: {e}")
                    continue
                    
            else:
                p = int(input("Введите p: "))
                g = int(input("Введите g: "))
                keys_file_path = input("Введите путь к файлу с ключами: ")
                try:
                    with open(keys_file_path, "r") as f:
                        count = int(f.readline().strip())
                        keys_used = []
                        for _ in range(count):
                            line = f.readline().strip()
                            x_a, x_b = map(int, line.split())
                            keys_used.append((x_a, x_b))
                    print(f"Загружено {len(keys_used)} пар ключей")
                except Exception as e:
                    print(f"Ошибка при загрузке ключей: {e}")
                    continue
            
            input_file = key_file_path[:-4]
            output_file = input("Введите путь для дешифрованного файла: ")
            
            try:
                vernam_decrypt_file(input_file, output_file, p, g, keys_used)
                print("Файл успешно дешифрован!")
            except Exception as e:
                print(f"Ошибка при дешифровании: {e}")
        
        elif choice == "3":
            print("Выход из программы")
            break
        
        else:
            print("Неверный выбор, попробуйте снова")

if __name__ == "__main__":
    main()