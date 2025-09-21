#include <stdio.h>
#include <unistd.h>
#include <signal.h>
#include <stdlib.h>

void handle_signal(int sig){
	printf("\033[?25h\033[0m");
	exit(0);	
}

int main(int argc, char *argv[]){
	
	signal(SIGINT, handle_signal);
	signal(SIGTERM, handle_signal);
	
	char sym = "*";
	int speed = 100;
	int color = 32;
	int dir = 1;
	int x = 1;
	
	for (int i=1; i<argc; i++){
		if (argv[i][0] = '-'){
			switch(argv[i][1]){
				case 's': sym = argv[++i][0]; break;
				case 'd': speed = atoi(argv[++i]); break;
				case 'c': color = atoi(argv[++i]); break;
				case 'r': dir = 1; break;
				case 'l': dir = -1; break;
				case 'x': x = atoi(argv[++i]); break;
			}
		}
	}
	
	printf("\033[?25l");
	
	while (1){
		if (access("/tmp/stop", F_OK) == 0){
			printf("\033[?25h\033[0m\033[2J");
			remove("/tmp/stop");
			exit(0);
		}
		
		printf("\033[1;%dH\033[%dm%c", x, color, sym);
		fflush(stdout);
		usleep(speed * 1000);
		printf("\033[1;%dH ", x);
		x+=dir;
		if (x>80 || x<1) x = dir > 0? 1:80;
	}
	return 0;
}