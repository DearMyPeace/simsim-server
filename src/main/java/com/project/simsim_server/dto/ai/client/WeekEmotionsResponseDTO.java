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
        Long happyCnt, Long appreciationCnt, Long loveCnt, Long positiveTotalCnt,
        Long tranquilityCnt, Long curiosityCnt, Long surpriseCnt, Long neutralTotalCnt,
        Long sadCnt, Long angryCnt, Long fearCnt, Long negativeTotalCnt

    ) {
        this.happyCnt = happyCnt.intValue();
        this.appreciationCnt = appreciationCnt.intValue();
        this.loveCnt = loveCnt.intValue();
        this.positiveTotalCnt = positiveTotalCnt.intValue();
        this.tranquilityCnt = tranquilityCnt.intValue();
        this.curiosityCnt = curiosityCnt.intValue();
        this.surpriseCnt = surpriseCnt.intValue();
        this.neutralTotalCnt = neutralTotalCnt.intValue();
        this.sadCnt = sadCnt.intValue();
        this.angryCnt = angryCnt.intValue();
        this.fearCnt = fearCnt.intValue();
        this.negativeTotalCnt = negativeTotalCnt.intValue();
    }
}

