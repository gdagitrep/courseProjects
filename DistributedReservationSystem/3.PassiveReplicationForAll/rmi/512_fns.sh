function gen_mw_replic_cfg()
{
  MIDW_REPLIC_CFG=""
	MIDW_REPLIC_CNT=$(echo ${#MIDW_RMI_REG_HOSTS[@]})

	cnt=0
	while [[ $cnt -lt $MIDW_REPLIC_CNT ]]
	do
	  rep_cfg="(${MIDW_RMI_REG_HOSTS[$cnt]}|${MIDW_RMI_PORTS[$cnt]}|${MIDW_RMI_OBJ_NAMES[$cnt]})"
		if [[ $cnt -eq 0 ]]
		then
		  MIDW_REPLIC_CFG="$rep_cfg"
		else
		  MIDW_REPLIC_CFG="${MIDW_REPLIC_CFG},$rep_cfg"
		fi

		let "cnt = $cnt + 1"
	done

	export MIDW_REPLIC_CFG MIDW_REPLIC_CNT
}

function gen_htl_replic_cfg()
{
  HTL_REPLIC_CFG=""
	HTL_REPLIC_CNT=$(echo ${#HTL_RM_RMI_HOSTS[@]})

	cnt=0
	while [[ $cnt -lt $HTL_REPLIC_CNT ]]
	do
	  rep_cfg="(${HTL_RM_RMI_HOSTS[$cnt]}|${HTL_RM_RMI_PORTS[$cnt]}|${HTL_RM_RMI_OBJ_NAMES[$cnt]})"
		if [[ $cnt -eq 0 ]]
		then
		  HTL_REPLIC_CFG="$rep_cfg"
		else
		  HTL_REPLIC_CFG="${HTL_REPLIC_CFG},$rep_cfg"
		fi

		let "cnt = $cnt + 1"
	done

	export HTL_REPLIC_CFG HTL_REPLIC_CNT
}



function gen_car_replic_cfg()
{
  CAR_REPLIC_CFG=""
	CAR_REPLIC_CNT=$(echo ${#CAR_RM_RMI_HOSTS[@]})

	cnt=0
	while [[ $cnt -lt $CAR_REPLIC_CNT ]]
	do
	  rep_cfg="(${CAR_RM_RMI_HOSTS[$cnt]}|${CAR_RM_RMI_PORTS[$cnt]}|${CAR_RM_RMI_OBJ_NAMES[$cnt]})"
		if [[ $cnt -eq 0 ]]
		then
		  CAR_REPLIC_CFG="$rep_cfg"
		else
		  CAR_REPLIC_CFG="${CAR_REPLIC_CFG},$rep_cfg"
		fi

		let "cnt = $cnt + 1"
	done

	export CAR_REPLIC_CFG CAR_REPLIC_CNT
}


function gen_flt_replic_cfg()
{
  FLT_REPLIC_CFG=""
	FLT_REPLIC_CNT=$(echo ${#FLT_RM_RMI_HOSTS[@]})

	cnt=0
	while [[ $cnt -lt $FLT_REPLIC_CNT ]]
	do
	  rep_cfg="(${FLT_RM_RMI_HOSTS[$cnt]}|${FLT_RM_RMI_PORTS[$cnt]}|${FLT_RM_RMI_OBJ_NAMES[$cnt]})"
		if [[ $cnt -eq 0 ]]
		then
		  FLT_REPLIC_CFG="$rep_cfg"
		else
		  FLT_REPLIC_CFG="${FLT_REPLIC_CFG},$rep_cfg"
		fi

		let "cnt = $cnt + 1"
	done

	export FLT_REPLIC_CFG FLT_REPLIC_CNT
}

function gen_tmgr_replic_cfg()
{
  TMGR_REPLIC_CFG=""
	TMGR_REPLIC_CNT=$(echo ${#TMGR_RMI_REG_HOSTS[@]})

	cnt=0
	while [[ $cnt -lt $TMGR_REPLIC_CNT ]]
	do
	  rep_cfg="(${TMGR_RMI_REG_HOSTS[$cnt]}|${TMGR_RMI_PORTS[$cnt]}|${TMGR_RMI_OBJ_NAMES[$cnt]})"
		if [[ $cnt -eq 0 ]]
		then
		  TMGR_REPLIC_CFG="$rep_cfg"
		else
		  TMGR_REPLIC_CFG="${TMGR_REPLIC_CFG},$rep_cfg"
		fi

		let "cnt = $cnt + 1"
	done

	export TMGR_REPLIC_CFG TMGR_REPLIC_CNT
}

