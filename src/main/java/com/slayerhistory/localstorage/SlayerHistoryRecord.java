package com.slayerhistory.localstorage;

import lombok.Data;

@Data
public class SlayerHistoryRecord
{
	public final long taskCompletionTime;
	public final String taskMaster;
	public final String taskName;
	public final int taskQuantity;
}
