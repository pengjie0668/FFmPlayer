

#ifndef FFMUSIC_PLAYSTATUS_H
#define FFMUSIC_PLAYSTATUS_H


class PlayStatus {

public:
    bool exit = false;
    bool load = true;
    bool seek = false;
    bool pause = false;

public:
    PlayStatus();
    ~PlayStatus();

};


#endif //FFMUSIC_PLAYSTATUS_H
