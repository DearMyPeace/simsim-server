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
        this.happyCnt = Integer.parseInt(String.valueOf(happyCnt));
        this.appreciationCnt = Integer.parseInt(String.valueOf(appreciationCnt));
        this.loveCnt = Integer.parseInt(String.valueOf(loveCnt));
        this.positiveTotalCnt = Integer.parseInt(String.valueOf(positiveTotalCnt));
        this.tranquilityCnt = Integer.parseInt(String.valueOf(tranquilityCnt));
        this.curiosityCnt = Integer.parseInt(String.valueOf(curiosityCnt));
        this.surpriseCnt = Integer.parseInt(String.valueOf(surpriseCnt));
        this.neutralTotalCnt = Integer.parseInt(String.valueOf(neutralTotalCnt));
        this.sadCnt = Integer.parseInt(String.valueOf(sadCnt));
        this.angryCnt = Integer.parseInt(String.valueOf(angryCnt));
        this.fearCnt = Integer.parseInt(String.valueOf(fearCnt));
        this.negativeTotalCnt = Integer.parseInt(String.valueOf(negativeTotalCnt));
    }
}

