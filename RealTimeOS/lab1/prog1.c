#include <stdio.h>
#include <stdlib.h>
#include <sys/ioctl.h>
#include <unistd.h>
#include <termios.h>

int main(){
	struct winsize w;
	ioctl(STDOUT_FILENO, TIOCGWINSZ, &w);
	int rows = w.ws_row;
	int cols = w.ws_col;
	
	printf("\033[?25l");
	printf("\033[2J");
	
	
	int center_row = rows / 2;
	int center_col = (cols - 5) / 2;
	
	printf("\033[%d;%dH", center_row, center_col);
	printf("HELLO");
	fflush(stdout);
	
	getchar();
	
	printf("\033[2J");
	printf("\033[?25h");
	printf("\033[%d;%dH", 1, 1);
	
	return 0;
}