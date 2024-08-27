package com.project.simsim_server.dto.ai.client;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class WeekEmotionsResponseDTO {
    private int happyCnt;
    private int appreciationCnt;
    private int loveCnt;
    private int positiveTotalCnt;
    private int tranquilityCnt;
    private int curiosityCnt;
    private int surpriseCnt;
    private int neutralTotalCnt;
    private int sadCnt;
    private int angryCnt;
    private int fearCnt;
    private int negativeTotalCnt;

    public WeekEmotionsResponseDTO(
        int happyCnt, int appreciationCnt, int loveCnt, int positiveTotalCnt,
        int tranquilityCnt, int curiosityCnt, int surpriseCnt, int neutralTotalCnt,
        int sadCnt, int angryCnt, int fearCnt, int negativeTotalCnt

    ) {
        this.happyCnt = happyCnt;
        this.appreciationCnt = appreciationCnt;
        this.loveCnt = loveCnt;
        this.positiveTotalCnt = positiveTotalCnt;
        this.tranquilityCnt = tranquilityCnt;
        this.curiosityCnt = curiosityCnt;
        this.surpriseCnt = surpriseCnt;
        this.neutralTotalCnt = neutralTotalCnt;
        this.sadCnt = sadCnt;
        this.angryCnt = angryCnt;
        this.fearCnt = fearCnt;
        this.negativeTotalCnt = negativeTotalCnt;
    }
}

