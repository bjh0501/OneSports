package com.familytoto.familytotoProject.ranking.service;

import java.util.List;

import com.familytoto.familytotoProject.ranking.domain.RankingVO;

public interface RankingService {
	// 경험치랭킹
	List<RankingVO> listExpRanking();
	
	// 게임랭킹
	List<RankingVO> listGameRanking(String gameName);
}
