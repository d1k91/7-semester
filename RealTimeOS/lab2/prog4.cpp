#include <iostream>
#include <unistd.h>
#include <stdio.h>
#include <signal.h>
#include <cstdlib>
#include <ctime>
#include <math.h>
#include <pthread.h>
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

pthread_mutex_t params_mutex = PTHREAD_MUTEX_INITIALIZER;

void sig_handler(int) {
    running = 0;
}

void* animFigure(void* arg) {
    pthread_setcancelstate(PTHREAD_CANCEL_ENABLE, NULL);
    Params* params = (Params*)arg;
    std::srand(std::time(NULL) ^ pthread_self()); 

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
        
        pthread_mutex_lock(&params_mutex);
        rho = params->a * cos(phi) + params->b;
        x = rho * cos(phi);
        y = rho * sin(phi);
        pthread_mutex_unlock(&params_mutex);

        screen_x = CENTER_X + static_cast<int>(SCALE * x);
        screen_y = CENTER_Y + static_cast<int>(SCALE * y);
        
        MoveTo(screen_x, screen_y, id);
        
        usleep(10000);
    }

    Delete(id);
    return NULL;
}

int main() {
    signal(SIGINT, sig_handler);

    ConnectGraph(0);

    Params params = {1.0, 2.0};

    pthread_t thread;
    if (pthread_create(&thread, NULL, animFigure, (void*)&params) != 0) {
        perror("pthread_create");
        return 1;
    }

    Text(10, 10, "q/w: +/- a, e/r: +/- b");

    bool loop = true;
    while (loop) {
        char c = InputChar();
        if (c != 0) {
            pthread_mutex_lock(&params_mutex);
            switch (c) {
                case 'q': params.a += 0.1; break;
                case 'w': params.a -= 0.1; if (params.a < 0.1) params.a = 0.1; break;
                case 'e': params.b += 0.1; break;
                case 'r': params.b -= 0.1; if (params.b < 0.1) params.b = 0.1; break;
                case 't': loop = false; break;
                default: break;
            }
            pthread_mutex_unlock(&params_mutex);
        }
        usleep(100000);
    }

    pthread_cancel(thread);
    pthread_join(thread, NULL);

    CloseGraph();
    pthread_mutex_destroy(&params_mutex);
    return 0;
}