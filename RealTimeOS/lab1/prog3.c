#include <stdio.h>
#include <unistd.h>
#include <termios.h>

int main(){
	struct termios old, new;
	tcgetattr(0, &old);
	new = old;
	new.c_lflag &= ~(ICANON | ECHO);
	tcsetattr(0, TCSANOW, &new);
	
	printf("\033[?25l");
	
	int x = 1;
	while(1) {
		printf("\033[1;%dH*", x);
		fflush(stdout);
		usleep(100000);
		printf("\033[1;%dH ", x);
		x++;
		if (x > 80) x = 1;
	}
	
	return 0;
	
}