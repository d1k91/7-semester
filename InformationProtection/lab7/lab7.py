from primeFerma import *
from Diffie_Hellman import *

def vernam(data, key):
    result = bytearray()
    key_bytes = key.to_bytes((key.bit_length() + 7) // 8, 'big')
    
    for i, byte in enumerate(data):
        key_byte = key_bytes[i % len(key_bytes)]
        result.append(byte ^ key_byte)
    
    return bytes(result)

def vernam_encrypt_file(input_file, output_file, key):
    with open(input_file, "rb") as f:
        data = f.read()
    
    encrypted = vernam(data, key)
    
    with open(output_file, "wb") as f:
        f.write(encrypted)

def vernam_decrypt_file(input_file, output_file, key):
    with open(input_file, "rb") as f:
        encrypted_data = f.read()
    
    decrypted = vernam(encrypted_data, key)
    
    with open(output_file, "wb") as f:
        f.write(decrypted)

def main(): 
    while True:
        print("\n1. Шифрование файла")
        print("2. Дешифрование файла")
        
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
                
                x_a = int(input("Введите Xa: "))
                x_b = int(input("Введите Xb: "))
                
            else:
                print("Генерация параметров Диффи-Хеллмана...")
                p, g, x_a, x_b = generate_dh_parameters()
                print(f"p = {p}")
                print(f"g = {g}")
                print(f"x_a = {x_a}")
                print(f"x_b = {x_b}")
            
            shared_key = Diffie_Hellman(p, g, x_a, x_b)
            print(f"Общий секретный ключ: {shared_key}")
            
            input_file = input("Введите путь к файлу для шифрования: ")
            output_file = input("Введите путь для зашифрованного файла: ")
            
            try:
                vernam_encrypt_file(input_file, output_file, shared_key)
                print("Файл успешно зашифрован!")
                
                with open(output_file + ".key", "w") as key_file:
                    key_file.write(f"{p}\n{g}\n{x_a}\n{x_b}")
                print(f"Параметры ключа сохранены в {output_file}.key")
                
            except Exception as e:
                print(f"Ошибка при шифровании: {e}")
        
        elif choice == "2":
            decrypt_method = input("1. Использовать сохраненные параметры ключа\n2. Ввести параметры вручную\n")
            
            if decrypt_method == "1":
                key_file_path = input("Введите путь к файлу с параметрами ключа: ")
                try:
                    with open(key_file_path, "r") as key_file:
                        p = int(key_file.readline().strip())
                        g = int(key_file.readline().strip())
                        x_a = int(key_file.readline().strip())
                        x_b = int(key_file.readline().strip())
                    
                    print("Параметры ключа загружены")
                    
                except Exception as e:
                    print(f"Ошибка при загрузке параметров ключа: {e}")
                    continue
                    
            else:
                p = int(input("Введите p: "))
                g = int(input("Введите g: "))
                x_a = int(input("Введите Xa: "))
                x_b = int(input("Введите Xb: "))
            
            shared_key = Diffie_Hellman(p, g, x_a, x_b)
            print(f"Общий секретный ключ: {shared_key}")
            
            input_file = input("Введите путь к зашифрованному файлу: ")
            output_file = input("Введите путь для дешифрованного файла: ")
            
            try:
                vernam_decrypt_file(input_file, output_file, shared_key)
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