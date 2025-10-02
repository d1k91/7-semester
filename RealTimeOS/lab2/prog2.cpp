#include <iostream>
#include <unistd.h>
#include <stdio.h>
#include <sys/wait.h>
#include <signal.h>
#include <cstdlib>
#include <ctime>
#include "vingraph.h"

volatile sig_atomic_t running = 1;

void sig_handler(int) {
    running = 0;
}

void animCircle() {
    signal(SIGTERM, sig_handler);
    std::srand(std::time(NULL) ^ getpid()); 
    
    int x = 200, y = 200;
    int width = 50, height = 50;
    int id = Ellipse(x, y, width, height);
    int dx = 3;
    const int left = 0, right = 640;
    int color_counter = 0;
    const int color_change_interval = 200;

    while (running) {
        Move(id, dx, 0);
        x += dx;
        if (x < left || x + width > right) dx = -dx;
        
        if (++color_counter >= color_change_interval) {
            Fill(id, RGB(std::rand() % 256, std::rand() % 256, std::rand() % 256));
            
            tPoint dim = GetDim(id);
            tPoint pos = GetPos(id);
            int new_width = dim.x + 10;
            int new_height = dim.y + 10;
            if (new_width > 150) new_width = 50;
            if (new_height > 150) new_height = 50;
            
            // Adjust position to keep within bounds
            x = pos.x;
            if (x + new_width > right) x = right - new_width;
            if (x < left) x = left;
            
            EnlargeTo(x, y, new_width, new_height, id);
            width = new_width;
            height = new_height;
            
            color_counter = 0;
        }
        
        usleep(20000);
    }

    CloseGraph();
}

void animRect() {
    signal(SIGTERM, sig_handler);
    std::srand(std::time(NULL) ^ getpid());
    
    int x = 100, y = 100;
    int width = 50, height = 50;
    int id = Rect(x, y, width, height);
    int dy = 10;
    const int top = 0, bottom = 480;
    int color_counter = 0;
    const int color_change_interval = 150;

    while (running) {
        Move(id, 0, dy);
        y += dy;
        if (y < top || y + height > bottom) dy = -dy;
        
        if (++color_counter >= color_change_interval) {
            Fill(id, RGB(std::rand() % 256, std::rand() % 256, std::rand() % 256));
            
            tPoint dim = GetDim(id);
            tPoint pos = GetPos(id);
            int new_width = dim.x + 10;
            int new_height = dim.y + 10;
            if (new_width > 150) new_width = 50;
            if (new_height > 150) new_height = 50;
            
            // Adjust position to keep within bounds
            y = pos.y;
            if (y + new_height > bottom) y = bottom - new_height;
            if (y < top) y = top;
            
            EnlargeTo(x, y, new_width, new_height, id);
            width = new_width;
            height = new_height;
            
            color_counter = 0;
        }
        
        usleep(120000);
    }

    CloseGraph();
}

int main() {
    ConnectGraph(0);

    pid_t p1 = fork();
    if (p1 == 0) {
        animCircle();
        _exit(0);
    }

    pid_t p2 = fork();
    if (p2 == 0) {
        animRect();
        _exit(0);
    }

    while (InputChar() == 0) { 
        usleep(100000);
    } 

    kill(p1, SIGTERM); 
    kill(p2, SIGTERM);

    waitpid(p1, NULL, 0);
    waitpid(p2, NULL, 0);

    CloseGraph();
    return 0;
}