from primeFerma import *

def Diffie_Hellman(p, g, x_a, x_b):
    y_a = modular_exponentiation(g, x_a, p)
    y_b = modular_exponentiation(g, x_b, p)

    z_ab = modular_exponentiation(y_b, x_a, p)
    z_ba = modular_exponentiation(y_a, x_b, p)

    # print(f'Zab = {z_ab}, Zba = {z_ba}')
    return z_ab

def generate_dh_parameters():
    power = 10**9
    p = random.randint(1, power)
    while not isPrimeFerma(p):
        p = random.randint(1, power)
    q = int((p-1)/2)
    g = random.randint(2, p - 1)
    while modular_exponentiation(g, q, p) == 1 and g < p-1:
        g = random.randint(2, p - 1)
    x_a = random.randint(1, p)
    x_b = random.randint(1, p)
    return p, g, x_a, x_b