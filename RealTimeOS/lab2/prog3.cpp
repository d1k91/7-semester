#include <iostream>
#include <unistd.h>
#include <stdio.h>
#include <sys/wait.h>
#include <signal.h>
#include <cstdlib>
#include <ctime>
#include <math.h>
#include <sys/mman.h>
#include <fcntl.h>
#include "vingraph.h"

#define CENTER_X 320
#define CENTER_Y 240
#define SCALE 100.0
#define PHI_STEP 0.01

volatile sig_atomic_t running = 1;

struct Params {
    double a;
    double b;
};

void sig_handler(int) {
    running = 0;
}

void animFigure(Params* params) {
    signal(SIGTERM, sig_handler);
    std::srand(std::time(NULL) ^ getpid());

    double phi = 0.0;
    double rho = params->a * cos(phi) + params->b;
    double x = rho * cos(phi);
    double y = rho * sin(phi);
    int screen_x = CENTER_X + static_cast<int>(SCALE * x);
    int screen_y = CENTER_Y + static_cast<int>(SCALE * y);
    
    int id = Ellipse(screen_x, screen_y, 10, 10);

    while (running) {
        phi += PHI_STEP;
        if (phi > 2 * M_PI) phi -= 2 * M_PI;
        
        rho = params->a * cos(phi) + params->b;
        x = rho * cos(phi);
        y = rho * sin(phi);
        screen_x = CENTER_X + static_cast<int>(SCALE * x);
        screen_y = CENTER_Y + static_cast<int>(SCALE * y);
        
        MoveTo(screen_x, screen_y, id);
        
        usleep(10000);
    }

    Delete(id);
    CloseGraph();
}

int main() {
    ConnectGraph(0);

    int fd = open("/tmp/params", O_CREAT | O_RDWR, 0666);
    if (fd < 0) {
        perror("open");
        return 1;
    }
    if (ftruncate(fd, sizeof(Params)) < 0) {
        perror("ftruncate");
        close(fd);
        return 1;
    }
    
    Params* params = (Params*) mmap(NULL, sizeof(Params), PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
    if (params == MAP_FAILED) {
        perror("mmap");
        close(fd);
        return 1;
    }

    params->a = 1.0;
    params->b = 2.0;

    pid_t pid = fork();
    if (pid == 0) {
        animFigure(params);
        munmap(params, sizeof(Params));
        close(fd);
        _exit(0);
    } else if (pid < 0) {
        perror("fork");
        return 1;
    }

    Text(10,10,"q/w: +/- a, e/r: +/- b ");

    bool loop = true;
    while (loop) {
        char c = InputChar();
        if (c != 0) {
            switch (c) {
                case 'q': params->a += 0.1; break;
                case 'w': params->a -= 0.1; if (params->a < 0.1) params->a = 0.1; break;  // Decrease a (min 0.1)
                case 'e': params->b += 0.1; break;
                case 'r': params->b -= 0.1; if (params->b < 0.1) params->b = 0.1; break;  // Decrease b (min 0.1)
                case 't': loop = false; break;
                default: break;
            }
        }
        usleep(100000);
    }

    kill(pid, SIGTERM);
    waitpid(pid, NULL, 0);
    munmap(params, sizeof(Params));
    close(fd);
    unlink("/tmp/params");
    CloseGraph();
    return 0;
}