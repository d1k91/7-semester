import hashlib
import os
import random
import json
from primeFerma import isPrimeFerma, modular_exponentiation
from EuclideanAlgorithm import Euclidean_algorithm, gcd


def generate_keys(power=10**9, silent=False):
    p = random.randint(2, power)
    while not isPrimeFerma(p):
        p = random.randint(2, power)
    if not silent:
        print(f'p = {p}')

    q = random.randint(2, power)
    while not isPrimeFerma(q):
        q = random.randint(2, power)
    if not silent:
        print(f'q = {q}')

    phi = (p - 1) * (q - 1)
    if not silent:
        print(f'ф = {phi}')

    d = random.randint(2, phi)
    while gcd(phi, d) != 1:
        d = random.randint(2, phi)
    if not silent:
        print(f"d (private) = {d}")

    N = p * q
    if not silent:
        print(f"N = {N}")

    _, c = Euclidean_algorithm(phi, d)
    if c < 0:
        c += phi
    if not silent:
        print(f"c (public) = {c}")

    return d, c, N, phi, p, q


def compute_hash(file_path, hash_algorithm='sha256'):
    if hash_algorithm not in hashlib.algorithms_guaranteed:
        raise ValueError(f"Неподдерживаемый алгоритм хеширования: {hash_algorithm}")

    hash_func = hashlib.new(hash_algorithm)
    with open(file_path, "rb") as f:
        while chunk := f.read(4096):
            hash_func.update(chunk)

    hash_digest = hash_func.digest()
    hash_hex = hash_func.hexdigest()
    return hash_digest, hash_hex


def sign_file(file_path, d, c, N, output_sig_path=None, hash_algorithm='sha256'):
    hash_digest, hash_hex = compute_hash(file_path, hash_algorithm)
    print(f"Хэш ({hash_algorithm}) для {file_path}: {hash_hex}")

    signatures = []
    for i, byte_val in enumerate(hash_digest):
        sig = modular_exponentiation(byte_val, d, N)
        signatures.append(sig)

    if output_sig_path is None:
        output_sig_path = file_path + '.sig'

    with open(output_sig_path, "w", encoding='utf-8') as sig_file:
        json.dump({
            'hash_algorithm': hash_algorithm,
            'N': N,
            'c': c,
            'signatures': signatures
        }, sig_file, ensure_ascii=False, indent=2)

    print(f"Подпись сохранена в {output_sig_path}")
    return signatures, output_sig_path


def verify_signature(file_path, sig_path, c, N, hash_algorithm='sha256'):

    if not os.path.exists(sig_path):
        raise FileNotFoundError(f"Файл подписи не найден: {sig_path}")

    with open(sig_path, "r", encoding='utf-8') as sig_file:
        data = json.load(sig_file)

    if data['N'] != N or data['c'] != c:
        print("Параметры подписи (N, c) не совпадают с текущими ключами")
        return False

    stored_alg = data.get('hash_algorithm', 'sha256')
    if stored_alg != hash_algorithm:
        print(f"Ожидался алгоритм {hash_algorithm}, в подписи — {stored_alg}")
        return False

    recovered_bytes = []
    for sig in data['signatures']:
        recovered_int = modular_exponentiation(sig, c, N)
        if not (0 <= recovered_int <= 255):
            print(f"Ошибка: восстановленный байт {recovered_int} вне диапазона 0-255")
            return False
        recovered_bytes.append(recovered_int)

    recovered_digest = bytes(recovered_bytes)

    computed_digest, _ = compute_hash(file_path, hash_algorithm)

    print(f"Рассчитанный хэш ({hash_algorithm}): {computed_digest.hex()}")
    print(f"Восстановленный хэш из ключей: {recovered_digest.hex()}")

    if recovered_digest == computed_digest:
        print("✅ Подпись действительна!")
        return True
    else:
        print("❌ Подпись недействительна!")
        return False


def main():
    print("1. Сгенерировать Ключи")
    print("2. Подписать файл")
    print("3. Проверить подпись")
    keys_generated = False
    d, c, N = None, None, None

    while True:
        choice = input("    ").strip()

        if choice == "1":
            d, c, N, _, _, _ = generate_keys()
            keys_generated = True
            print("Ключи сгенерированы")

        elif choice == "2":
            if not keys_generated:
                print("Сначала сгенерируйте ключи")
                continue
            file_path = input("Путь до файла: ").strip()
            if not os.path.exists(file_path):
                print("Файл не найден")
                continue
            sig_path = input("Путь до подписи (default: file.sig): ").strip() or None
            hash_alg = input("Выберите хэш-функцию (default: sha256): ").strip() or 'sha256'
            sign_file(file_path, d, c, N, sig_path, hash_alg)

        elif choice == "3":
            if not keys_generated:
                print("Сначала сгенерируйте ключи")
                continue
            file_path = input("Путь до файла для проверки подписи: ").strip()
            if not os.path.exists(file_path):
                print("Файл не найден")
                continue
            sig_path = input("Путь до подписи: ").strip()
            hash_alg = input("Использованная хэш-функция (default: sha256): ").strip() or 'sha256'
            verify_signature(file_path, sig_path, c, N, hash_alg)


if __name__ == "__main__":
    main()