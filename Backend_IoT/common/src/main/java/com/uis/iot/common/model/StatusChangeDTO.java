package com.uis.iot.common.model;

import java.util.Date;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StatusChangeDTO {

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
	private Date sentDate;

	private long alive;

	private long death;

	public StatusChangeDTO() {
		super();
	}

	public StatusChangeDTO(Date timestamp, long alive, long death) {
		super();
		this.sentDate = timestamp;
		this.alive = alive;
		this.death = death;
	}

	public Date getTimestamp() {
		return sentDate;
	}

	public void setTimestamp(Date timestamp) {
		this.sentDate = timestamp;
	}

	public long getAlive() {
		return alive;
	}

	public void setAlive(long alive) {
		this.alive = alive;
	}

	public long getDeath() {
		return death;
	}

	public void setDeath(long death) {
		this.death = death;
	}

	@Override
	public int hashCode() {
		return Objects.hash(sentDate, alive, death);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof StatusChangeDTO) {
			final StatusChangeDTO other = (StatusChangeDTO) obj;
			return Objects.equals(sentDate, other.getTimestamp()) && alive == other.getAlive()
					&& death == other.getDeath();
		}
		return false;
	}

	@Override
	public String toString() {
		return "[timestamp=" + sentDate + ", alive=" + alive + ", death=" + death + "]";
	}

}
