package com.online.casino.domain.dto;

import com.online.casino.domain.enums.GameType;

/**
 * User: Bjorn Harvold
 * Date: Oct 11, 2009
 * Time: 11:59:35 PM
 * Responsibility:
 */
public final class GameFinder {
    private GameType type;
    private String stakeId;

    public GameType getType() {
        return type;
    }

    public void setType(GameType type) {
        this.type = type;
    }

    public String getStakeId() {
        return stakeId;
    }

    public void setStakeId(String stakeId) {
        this.stakeId = stakeId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("GameFinder");
        sb.append(", type=").append(type);
        sb.append(", stakeId='").append(stakeId).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
