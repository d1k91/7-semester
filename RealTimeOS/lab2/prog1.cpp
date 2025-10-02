#include <stdlib.h>
#include <unistd.h>
#include "vingraph.h"

int main() 
{
    ConnectGraph();

    Text(10, 10, "Pixel");
    Pixel(20, 40);

    Text(70, 10, "Line");
    Line(70, 50, 130, 90);

    Text(140, 10, "Polyline");
    tPoint pl[] = {{140, 100}, {200, 40}, {200, 100}, {155, 55}};
    Polyline(pl, 4);

    Text(210, 10, "Rect");
    Rect(210, 40, 60, 60);

    Text(280, 10, "Polygon");
    tPoint p2[] = {{280, 100}, {310, 40}, {340, 100}};
    Polygon(p2, 3);

    Text(385, 10, "Ellipse");
    Ellipse(350, 40, 60, 60);
    Ellipse(420, 50, 60, 40);

    Text(560, 10, "Grid");
    Grid(560, 40, 60, 60, 3, 2);

    char c = 0;
    while(!c) {
        c = InputChar(); 
        delay(10);
    }

    CloseGraph();
    return 0;
}