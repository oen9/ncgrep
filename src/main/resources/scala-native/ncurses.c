#include <ncurses.h>

unsigned int scalanative_a_underline() { return A_UNDERLINE; }

int scalanative_key_up() { return KEY_UP; }
int scalanative_key_down() { return KEY_DOWN; }
int scalanative_key_left() { return KEY_LEFT; }
int scalanative_key_right() { return KEY_RIGHT; }
int scalanative_key_enter() { return KEY_ENTER; }
int scalanative_key_dc() { return KEY_DC; }
int scalanative_key_backspace() { return KEY_BACKSPACE; }
int scalanative_key_resize() { return KEY_RESIZE; }

int scalanative_key_f(int n) { return KEY_F(n); }
