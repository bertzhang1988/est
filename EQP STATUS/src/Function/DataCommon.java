package Function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DataCommon {

	// in ldg without pro
	public static String query1 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] in ('ldg')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// in ldg with pro
	public static String query2 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] in ('ldg')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// can transit to ldg not in ldg without pro
	public static String query3 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] not in ('ldg') "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// can transit to ldg not in (ldg ldd) without pro
	public static String query23 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] not in ('ldg','ldd') "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// can transit to ldg not in ldg with pro
	public static String query4 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] not in ('ldg')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT  order by newid()";

	// can transit to ldg not in (ldg ldd) with pro
	public static String query24 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] not in ('ldg','ldd') and neqps.[Equipment_Status_Type_CD] ='cpu'"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// in ldg with HEADLOAD pro
	public static String query20 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null and wb.Headload_IN='y'"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] in ('ldg')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// in ldd with pro
	public static String query5 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd' and neqps.[Equipment_Status_Type_CD] in ('ldd')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in ldd without pro
	public static String query6 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd' and neqps.[Equipment_Status_Type_CD] in ('ldd')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// can transition to ldd not ldg ldd with pro
	public static String query7 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd' and neqps.[Equipment_Status_Type_CD] not in ('ldd','ldg') "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC order by newid()";

	// can transition to ldd in ldg or ldd with pro
	public static String query35 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd' and neqps.[Equipment_Status_Type_CD]  in ('ldd','ldg') "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC order by newid()";

	// can transition to ldd NOT IN LDD without pro
	public static String query8 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd' and neqps.[Equipment_Status_Type_CD] not in ('ldd')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// in ldd with HEADLOAD pro
	public static String query21 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null and wb.Headload_IN='y'"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd' and neqps.[Equipment_Status_Type_CD] in ('ldd')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// can transit to ldg not in ldg and ldd with pro
	public static String query9 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] not in ('ldg','ldd')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT having count(wb.pro_nb)=1 order by newid()";

	// in arr and enr without pro
	public static String query10 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.Dispatch_Dest_Facility_CD"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Dispatch_Dest_Facility_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Dispatch_Dest_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Dispatch_Dest_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD] in ('arr', 'enr')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.Dispatch_Dest_Facility_CD"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// in arr and enr with pro
	public static String query11 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Dispatch_Dest_Facility_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Dispatch_Dest_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Dispatch_Dest_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD] in ('arr', 'enr')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Dispatch_Dest_Facility_CD order by newid()";

	// TRAILER CAN TRANSIT TO UAD not from ONH
	public static String query12 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='UAD' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD <> 'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " order by newid()";

	// TRAILER IN CAN TRANSIT TO UAD FROM ONH
	public static String query13 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='UAD' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD = 'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " order by newid()";

	// TRAILER NOT in ONH IN LDG WITH PRO
	public static String query14 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,NEQPS.[M204_Equipment_Status_Type_CD]"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,[M204_Equipment_Status_Type_CD]"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,EQPS.[M204_Equipment_Status_Type_CD],rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='UAD'  AND NEQPS.M204_Equipment_Status_Type_CD <> 'ONH'"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,NEQPS.[M204_Equipment_Status_Type_CD] order by newid()";

	// TRAILER NOT in ONH IN LDG WITHOUT PRO
	public static String query15 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,ESI.[M204_Equipment_Status_Type_CD]"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,NEQPS.[M204_Equipment_Status_Type_CD]"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,[M204_Equipment_Status_Type_CD]"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,EQPS.[M204_Equipment_Status_Type_CD],rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='UAD'  AND NEQPS.M204_Equipment_Status_Type_CD <> 'ONH'"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,ESI.[M204_Equipment_Status_Type_CD]"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// trailer can transit to mty without pro from onh
	public static String query16 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='MTY' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD = 'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// trailer can transit to mty with pro from onh
	public static String query17 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='MTY' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD ='ONH' "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// trailer can transit to mty and M204_Equipment_Status_Type_CD not from onh
	// without pro
	public static String query36 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='MTY' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD <>'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// trailer can transit to mty with pro and M204_Equipment_Status_Type_CD not
	// from onh with pro
	public static String query37 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='MTY' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD <>'ONH' "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// trailer can transit to BOR without pro from onh
	public static String query18 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='BOR' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD = 'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// trailer can transit to BOR with pro from onh
	public static String query19 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='BOR' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD ='ONH' "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// trailer can transit to BOR and M204_Equipment_Status_Type_CD not from onh
	// without pro
	public static String query38 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='BOR' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD <>'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// trailer can transit to BOR with pro and M204_Equipment_Status_Type_CD not
	// from onh with pro
	public static String query39 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='BOR' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD <>'ONH' "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// trailer can transit to CPU from onh
	public static String query25 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CPU' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD = 'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " order by newid()";

	// trailer can transit to CPU not from onh
	public static String query26 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CPU' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and M204_Equipment_Status_Type_CD <> 'ONH'"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " order by newid()";

	// valid terminal
	public static String query27 = " select top 10 * from EQP.facility_status_vw fs1, eqp.facility_vw ef where fs1.facility_Status_Effective_DT=(select max(fs2.Facility_Status_Effective_DT) from EQP.facility_status_vw fs2 where fs1.Company_CD=fs2.Company_CD AND  fs1.Facility_CD=fs2.Facility_CD and fs1.company_cd in ('002','185')) "
			+ " and (ltrim(fs1.facility_status_nm) in ('active','open 2wk') and  fs1.closed_in<>'y'  and ef.Facility_Type_NM   in ('port','railhead','terminal','rex salvage store','relay','breakbulk') ) and fs1.company_cd in ('002','185') and fs1.Company_CD=ef.Company_CD AND  fs1.Facility_CD=ef.Facility_CD ";

	// invalid terminal
	public static String query28 = "select top 10 * from EQP.facility_status_vw fs1, eqp.facility_vw ef where fs1.facility_Status_Effective_DT=(select max(fs2.Facility_Status_Effective_DT) from EQP.facility_status_vw fs2 where fs1.Facility_CD=fs2.Facility_CD and fs1.company_cd in ('002','185'))"
			+ " and (ltrim(fs1.facility_status_nm) not in( 'active','open 2wk') or fs1.closed_in='y'  or ef.Facility_Type_NM  not in ('port','railhead','terminal','rex salvage store','relay','breakbulk') )"
			+ " and fs1.company_cd in ('002','185') and fs1.Company_CD=ef.Company_CD AND  fs1.Facility_CD=ef.Facility_CD ";

	// valid destination
	public static String query29 = "select TOP 10 * from [EQP].[Facility_Status_vw] as efs,eqp.facility_vw as ef where ef.company_cd=efs.company_cd and ef.facility_cd=efs.facility_cd "
			+ " and ef.facility_cd=efs.facility_cd and efs.facility_Status_Effective_DT=(select max(fs2.Facility_Status_Effective_DT) from EQP.facility_status_vw fs2 where /*efs.Company_CD=fs2.Company_CD and*/  efs.Facility_CD=fs2.Facility_CD and company_cd in ('002','185')) "
			+ " and  ltrim(efs.facility_status_nm) in( 'active','open 2wk') and closed_in!='y' and efs.company_cd in ('002','185') and ef.Facility_Type_NM in ('terminal','rex salvage store','breakbulk')  order by newid()";

	// in valid destination
	public static String query30 = " select top 10 ef.company_cd,ef.facility_cd,ef.facility_type_nm,efs.facility_status_nm,efs.closed_in from eqp.facility as ef ,EQP.facility_status as efs"
			+ " where ef.company_cd=efs.company_cd and ef.facility_cd=efs.facility_cd and efs.facility_Status_Effective_DT=(select max(fs2.Facility_Status_Effective_DT) from EQP.facility_status fs2 where /*efs.Company_CD=fs2.Company_CD and*/  efs.Facility_CD=fs2.Facility_CD and company_cd in ('002','185'))"
			+ " and (ltrim(efs.facility_status_nm) not in( 'active','open 2wk') or efs.closed_in='y' or ef.facility_type_nm not in ('terminal','rex salvage store','breakbulk')) and ef.company_cd in ('002','185') order by newid()";

	// Maintenance Trailer
	public static String query31 = "select top 10 neqps.[Statusing_Facility_CD], e.equipment_unit_nb, e.Standard_Carrier_Alpha_CD, ea.Equipment_Avbl_Status_NM from eqp.equipment_vw e , EQP.Equipment_Availability_vw ea,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps		"
			+ " where e.equipment_unit_nb = ea.equipment_unit_nb and e.Standard_Carrier_Alpha_CD=ea.Standard_Carrier_Alpha_CD and ea.equipment_unit_nb = neqps.equipment_unit_nb and ea.Standard_Carrier_Alpha_CD=neqps.Standard_Carrier_Alpha_CD"
			+ " and ea.Equipment_Avbl_Status_NM='AVAILABLE' and e.Emergency_Repair_Due_IN='y' and e.Equipment_Type_NM in('trailer','STRAIGHT TRUCK') AND EA.M204_Occurrence_NB=(SELECT Min(M204_Occurrence_NB) FROM EQP.Equipment_Availability EA2 WHERE eA.equipment_unit_nb = ea2.equipment_unit_nb and eA.Standard_Carrier_Alpha_CD=ea2.Standard_Carrier_Alpha_CD ) order by newid()";

	// retired Trailer
	public static String query32 = "select top 10 e.equipment_unit_nb  from eqp.equipment_vw e, EQP.Equipment_Availability_vw ea "
			+ " where e.equipment_unit_nb = ea.equipment_unit_nb and e.Standard_Carrier_Alpha_CD=ea.Standard_Carrier_Alpha_CD and ea.Equipment_Avbl_Status_NM in ('retired','onfile') and e.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
			+ " AND EA.M204_Occurrence_NB=(SELECT Min(M204_Occurrence_NB) FROM EQP.Equipment_Availability EA2 WHERE eA.equipment_unit_nb = ea2.equipment_unit_nb and eA.Standard_Carrier_Alpha_CD=ea2.Standard_Carrier_Alpha_CD )"
			+ " and e.equipment_unit_nb=(select e2.equipment_unit_nb from eqp.equipment e2 where e.equipment_unit_nb = e2.equipment_unit_nb group by e2.equipment_unit_nb having  count(Standard_Carrier_Alpha_CD)=1) order by newid()";

	// ldg trailer with food pro no pois pro
	static String query33 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst, EQP.Waybill_vw wb, EQP.Waybill_Service_VW WBS,"
			+ " (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] ='ldg'"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and neqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and neqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and wb.PRO_NB=WBS.PRO_NB AND  WBS.SERVICE_CD ='food'"
			+ " and not exists(select 1 from (select wb.Standard_Carrier_Alpha_CD,wb.Equipment_Unit_NB from EQP.Waybill wb,EQP.Waybill_Service WBS where wb.PRO_NB=WBS.PRO_NB AND WBS.SERVICE_CD in('pois')) pois "
			+ " where neqps.Standard_Carrier_Alpha_CD = pois.Standard_Carrier_Alpha_CD  and neqps.Equipment_Unit_NB = pois.Equipment_Unit_NB)"
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// ldg trailer with pois pro no food pro
	public static String query34 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst, EQP.Waybill_vw wb, EQP.Waybill_Service_VW WBS,"
			+ " (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] ='ldg'"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and neqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and neqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and wb.PRO_NB=WBS.PRO_NB "
			+ " and not exists(select 1 from (select wb.Standard_Carrier_Alpha_CD,wb.Equipment_Unit_NB from EQP.Waybill wb,EQP.Waybill_Service WBS where wb.PRO_NB=WBS.PRO_NB AND WBS.SERVICE_CD in('food','pois')) pois "
			+ " where neqps.Standard_Carrier_Alpha_CD = pois.Standard_Carrier_Alpha_CD  and neqps.Equipment_Unit_NB = pois.Equipment_Unit_NB)"
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// can transit to ofd not in ofd without pro
	public static String query40 = "select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
			+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='OFD' and neqps.[Equipment_Status_Type_CD] not in ('ofd')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
			+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
			+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
			+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// can transit to ofd not in ofd with pro
	public static String query41 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ofd' and neqps.[Equipment_Status_Type_CD] not in ('ofd')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT order by newid()";

	// can transit to cl not in CL without pro
	public static String query42 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL' and neqps.[Equipment_Status_Type_CD] not in ('CL')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// can transit to CL not in CL and cltg with pro
	public static String query43 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL' and neqps.[Equipment_Status_Type_CD] not in ('CL','cltg')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT order by newid()";

	// in cl without pro
	public static String query44 =

			"select top 1  ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null AND NEQPS.City_Route_NM IS NOT NULL AND NEQPS.City_Route_Type_NM IS NOT NULL"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL'and neqps.[Equipment_Status_Type_CD]  in ('cl')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// in ldg with pro and some of pros are not loading but like pick up
	public static String query45 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null and wb.Headload_IN='N' "
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] in ('ldg')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM <> 'LOADING'"
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// in CL with pro
	public static String query46 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL' and neqps.[Equipment_Status_Type_CD] in ('CL')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] AND NEQPS.City_Route_NM IS NOT NULL AND NEQPS.City_Route_Type_NM IS NOT NULL"
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT ,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in CLTG with pro
	public static String query47 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT,Equipment_Origin_Facility_CD"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,eqps.Equipment_Origin_Facility_CD, rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CLTG' and neqps.[Equipment_Status_Type_CD] in ('CLTG')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in CL with pro and some of pros are not loading but like pick up
	public static String query48 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null "
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='cl' and neqps.[Equipment_Status_Type_CD] in ('cl')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM <> 'LOADING'"
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT order by newid()";

	// can transit to CLTG not in cltg and cl with pro
	public static String query49 = "select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CLTG' and  neqps.[Equipment_Status_Type_CD] not in ('CLTG','CL') "
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT order by newid()";

	// can transit to cltg not in CLTG without pro
	public static String query50 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CLTG' and neqps.[Equipment_Status_Type_CD] not in ('CLTG')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// in cl without pro in canadian terminal
	public static String query51 =

			"select top 1  ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.facility_vw F,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null AND NEQPS.City_Route_NM IS NOT NULL AND NEQPS.City_Route_Type_NM IS NOT NULL"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL'and neqps.[Equipment_Status_Type_CD]  in ('cl') and neqps.[Statusing_Facility_CD]=F.Facility_CD and f.Country_abbreviated_NM='CAN'"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// can transit to cl include cl without pro
	public static String query52 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// in BOR with pro
	public static String query53 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('BOR')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in MTY with pro
	public static String query54 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('MTY')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in CPU with pro
	public static String query55 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.City_Route_NM,neqps.City_Route_Type_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD,City_Route_NM,City_Route_Type_NM"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,eqps.City_Route_NM,eqps.City_Route_Type_NM,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('CPU')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,neqps.City_Route_NM,neqps.City_Route_Type_NM order by newid()";

	// in ofd with pro
	public static String query56 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.City_Route_NM,neqps.City_Route_Type_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD,City_Route_NM,City_Route_Type_NM"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,eqps.City_Route_NM,eqps.City_Route_Type_NM,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('OFD')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,neqps.City_Route_NM,neqps.City_Route_Type_NM order by newid()";

	// in uad with pro
	public static String query57 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('UAD')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in enr with pro
	public static String query58 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,Neqps.Dispatch_Dest_Facility_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD,Dispatch_Dest_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,eqps.Dispatch_Dest_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('ENR')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,Neqps.Dispatch_Dest_Facility_CD order by newid()";

	// in ARR with pro
	public static String query59 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('ARR')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in ARV with pro
	public static String query60 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa, eqp.Waybill_VW wb,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null  "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  AND neqps.[Equipment_Status_Type_CD] in ('ARV')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD  and wb.Waybill_Transaction_Type_NM = 'LOADING' "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.Equipment_Origin_Facility_CD,EQP.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// in spt with pro
	public static String query61 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB] AND neqps.[Equipment_Status_Type_CD] in ('SPT')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and neqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and neqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT ,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM order by newid()";

	// can transit to cl not in cl with inbond pro TO US

	public static String query62 = " select TOP 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Statusing_Facility_CD,f.Country_Abbreviated_NM"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,EQP.Facility f,SHIP.Shipment_Characteristic_vw sc, (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL' and neqps.[Equipment_Status_Type_CD] not in ('CL')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " and neqps.[Statusing_Facility_CD]=F.Facility_CD  and WB.Shipment_Characteristic_KEY = sc.Shipment_Characteristic_KEY AND (sc.Shipment_Is_In_Bond_IN = 'Y'  )"
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,f.Country_Abbreviated_NM order by newid()";

	// can transit to cl not in cl with inbond pro to canada

	public static String query63 = " select TOP 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Statusing_Facility_CD,f.Country_Abbreviated_NM"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,EQP.Facility f,SHIP.Shipment_Characteristic_vw sc,EQP.Waybill_Delivery_Codeword_VW wdc, (select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='CL' and neqps.[Equipment_Status_Type_CD] not in ('CL')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
			+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
			+ " and neqps.[Statusing_Facility_CD]=F.Facility_CD and WB.Pro_NB = wdc.Pro_NB  and WB.Shipment_Characteristic_KEY = sc.Shipment_Characteristic_KEY "
			+ " and (sc.Shipment_Is_Cntry_Mismatch_IN = 'Y' AND f.Country_Abbreviated_NM = 'CAN' AND wdc.Pro_NB not in (Select wdc2.Pro_NB FROM EQP.Waybill_Delivery_Codeword wdc2 Where wdc.Pro_NB = wdc2.Pro_NB AND wdc2.Delivery_Codeword_CD in ('PARS','RCOD','RA49','SZCU','RCSA','CL')))"
			+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,f.Country_Abbreviated_NM order by newid()";

	// can transition to ldd in ldd or ldg with pro has invalid weight
	public static String query64 =

			"select top 10 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldd' and neqps.[Equipment_Status_Type_CD]  in ('ldd','ldg')  "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD and (wb.Total_Actual_Weight_QT IN ( 0, 1) or wb.Total_Actual_Weight_QT is null)"
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC order by newid()";

	// CLTG TRAILER without pro
	public static String query65 = "select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
			+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT"
			+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT"
			+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
			+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
			+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB] and neqps.[Equipment_Status_Type_CD] in ('cltg')"
			+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
			+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
			+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
			+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT"
			+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// trailer can transit to STOR without pro
	public static String query66 =

			"select top 1 ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.M204_Equipment_Status_Type_CD"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK')  and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='STOR' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	// trailer can transit to STOR with pro
	public static String query67 =

			"select top 1 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,M204_Equipment_Status_Type_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.M204_Equipment_Status_Type_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='STOR' "
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB]  "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
					+ " and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
					+ " group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT order by newid()";

	// in stor without pro
	public static String query68 =

			"select top 1  ESi.Equipment_Unit_NB,ESi.Standard_Carrier_Alpha_CD,ESi.Statusing_Facility_CD,ESI.Equipment_Status_TS, ESi.Equipment_Status_Type_CD, ESi.Actual_Capacity_Consumed_PC,esi.Seal_NB,ESi.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " from (select  neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.[Statusing_Facility_CD],NEQPS.Equipment_Status_TS,Neqps.Actual_Capacity_Consumed_PC,Neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,neqps.City_Route_NM,neqps.City_Route_Type_NM,neqps.Planned_Delivery_DT,Neqps.Equipment_Origin_Facility_CD,eqp.equipment_Exterior_Length_QT,eqp.Primary_Use_NM"
					+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT,City_Route_NM,City_Route_Type_NM,Planned_Delivery_DT,Equipment_Origin_Facility_CD"
					+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,eqps.City_Route_NM,eqps.City_Route_Type_NM,eqps.Planned_Delivery_DT,eqps.Equipment_Origin_Facility_CD,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps"
					+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') "
					+ " and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB] AND neqps.[Equipment_Status_Type_CD]  in ('STOR')"
					+ " and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
					+ " and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)) esi"
					+ " LEFT JOIN EQP.Waybill_vw WB on  ESi.Equipment_Unit_NB=wb.Equipment_Unit_NB  and ESi.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD"
					+ " group by ESi.Statusing_Facility_CD,ESi.Standard_Carrier_Alpha_CD,ESi.Actual_Capacity_Consumed_PC,ESi.Equipment_Unit_NB,esi.Equipment_Status_Type_CD,ESi.Equipment_Dest_Facility_CD,esi.Seal_NB,ESI.Equipment_Status_TS,esi.Headload_Dest_Facility_CD,esi.Headload_Capacity_Consumed_PC,esi.Observed_Shipment_QT,esi.Observed_Weight_QT,esi.City_Route_NM,esi.City_Route_Type_NM,esi.Planned_Delivery_DT,esi.Equipment_Origin_Facility_CD,esi.equipment_Exterior_Length_QT,esi.Primary_Use_NM"
					+ " having COUNT(wb.pro_nb)=0 order by newid()";

	public static LinkedHashSet<ArrayList<String>> GetProListLD(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,wb.Manifest_Destination_Fclty_CD,wb.Headload_IN "
				+ " from eqp.waybill_vw wb  where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB]  ";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		PreparedStatement stat2 = conn2.prepareStatement(query6);
		ResultSet rs = stat2.executeQuery();
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		stat2 = conn2.prepareStatement(query10);
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-"
					+ PRONB.substring(9, PRONB.length());
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			String MANID = rs.getString("Manifest_Destination_Fclty_CD");
			String HLI = rs.getString("Headload_IN");
			if (HLI != null)
				if (!HLI.equalsIgnoreCase("Y"))
					HLI = null;

			stat2.setString(1, PRONB);
			stat2.setString(2, "flag");
			ResultSet rs5 = stat2.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}
			rs5.close();

			// get services
			stat2.setString(1, PRONB);
			stat2.setString(2, "serv");
			ResultSet rs6 = stat2.executeQuery();
			ArrayList<String> Services = new ArrayList<String>();
			while (rs6.next()) {
				String ServiceName = rs6.getString("Shipment_Service_Sub_Type_NM");
				Services.add(ServiceName);
				Services.removeAll(Collections.singleton(null));
			}
			rs6.close();
			String SERV;
			if (Services.size() != 0) {
				SERV = Services.toString().replaceAll("[\\[\\] ]", "");// .replaceAll(",
																		// ", //
																		// ",");
			} else {
				SERV = null;
			}

			ArrayList<String> c = new ArrayList<String>();
			c.add(pronb);
			c.add(SERV);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			c.add(MANID);
			c.add(HLI);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat2 != null)
			stat2.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static LinkedHashSet<ArrayList<String>> GetProListCLTG(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,wb.Manifest_Destination_Fclty_CD,wb.Headload_IN "
				+ " from eqp.waybill_vw wb  where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB] DESC ";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		PreparedStatement stat = conn2.prepareStatement(query6);
		ResultSet rs = stat.executeQuery();
		stat = conn2.prepareStatement(query10);
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd");
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-"
					+ PRONB.substring(9, PRONB.length());
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			stat.setString(1, PRONB);
			stat.setString(2, "flag");
			ResultSet rs5 = stat.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			rs5.close();
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}

			// get services
			stat.setString(1, PRONB);
			stat.setString(2, "serv");
			ResultSet rs6 = stat.executeQuery();
			ArrayList<String> Services = new ArrayList<String>();
			while (rs6.next()) {
				String ServiceName = rs6.getString("Shipment_Service_Sub_Type_NM");
				Services.add(ServiceName);
				Services.removeAll(Collections.singleton(null));
			}
			rs6.close();
			String SERV;
			if (Services.size() != 0) {
				SERV = Services.toString().replaceAll("[\\[\\] ]", "");// .replaceAll(",
																		// ", //
																		// ",");
			} else {
				SERV = null;
			}
			Timestamp Shipment_Due_DT = rs.getTimestamp("Shipment_Due_DT");
			String ShipDueDT = null;
			if (Shipment_Due_DT != null)
				ShipDueDT = dateFormat.format(Shipment_Due_DT);
			ArrayList<String> c = new ArrayList<String>();
			c.add(pronb);
			c.add(SERV);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			c.add(ShipDueDT);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static LinkedHashSet<ArrayList<String>> GetProListCL(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,wb.Manifest_Destination_Fclty_CD,wb.Headload_IN "
				+ " from eqp.waybill_vw wb  where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB]  ";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		PreparedStatement stat = conn2.prepareStatement(query6);
		ResultSet rs = stat.executeQuery();
		stat = conn2.prepareStatement(query10);
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-"
					+ PRONB.substring(9, PRONB.length());
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			String MANID = rs.getString("Manifest_Destination_Fclty_CD");
			stat.setString(1, PRONB);
			stat.setString(2, "flag");
			ResultSet rs5 = stat.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			rs5.close();

			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}

			// get services
			stat.setString(1, PRONB);
			stat.setString(2, "serv");
			ResultSet rs6 = stat.executeQuery();
			ArrayList<String> Services = new ArrayList<String>();
			while (rs6.next()) {
				String ServiceName = rs6.getString("Shipment_Service_Sub_Type_NM");
				Services.add(ServiceName);
				Services.removeAll(Collections.singleton(null));
			}
			rs6.close();
			String SERV;
			if (Services.size() != 0) {
				SERV = Services.toString().replaceAll("[\\[\\] ]", "");// .replaceAll(",
																		// ", //
																		// ",");
			} else {
				SERV = null;
			}
			ArrayList<String> c = new ArrayList<String>();
			c.add(pronb);
			c.add(SERV);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			c.add(MANID);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static LinkedHashSet<ArrayList<String>> GetHLProList(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,wb.Manifest_Destination_Fclty_CD,wb.Headload_IN "
				+ " from eqp.waybill_vw wb  where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB]  ";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		ResultSet rs = stat.executeQuery(query6);
		PreparedStatement stat2 = conn2.prepareStatement(query10);
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-"
					+ PRONB.substring(9, PRONB.length());
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			String MANID = rs.getString("Manifest_Destination_Fclty_CD");
			String HLI = rs.getString("Headload_IN");
			if (HLI != null)
				if (!HLI.equalsIgnoreCase("Y"))
					HLI = null;
			stat2.setString(1, PRONB);
			stat2.setString(2, "flag");
			ResultSet rs5 = stat2.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}
			ArrayList<String> c = new ArrayList<String>();
			c.add(HLI);
			c.add(pronb);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			c.add(MANID);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static LinkedHashSet<ArrayList<String>> GetProListLOBR(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();

		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,wb.Manifest_Destination_Fclty_CD,wb.Headload_IN "
				+ " from eqp.waybill_vw wb " + " where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB]  ";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		ResultSet rs = stat.executeQuery(query6);
		PreparedStatement stat2 = conn2.prepareStatement(query10);
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-"
					+ PRONB.substring(9, PRONB.length());
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			// String MANID=rs.getString("Manifest_Destination_Fclty_CD");
			// String HLI=rs.getString("Headload_IN");
			stat2.setString(1, PRONB);
			stat2.setString(2, "flag");
			ResultSet rs5 = stat2.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}
			ArrayList<String> c = new ArrayList<String>();
			c.add(pronb);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			// c.add(MANID);
			// c.add(HLI);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static LinkedHashSet<ArrayList<String>> GetProNotInLoadingListLOBR(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();

		String query6 = "select wb.pro_nb, wb.Origin_Facility_CD,wb.Planned_Destination_Facility_CD,wb.Total_Handling_Units_QT,wb.Total_Actual_Weight_QT,wb.Shipment_Due_DT,wb.Manifest_Destination_Fclty_CD,wb.Headload_IN,wb.Waybill_Transaction_Type_NM "
				+ " from eqp.waybill_vw wb " + " where wb.Standard_Carrier_Alpha_CD= '" + SCAC
				+ "'  and wb.Equipment_Unit_NB= '" + TrailerNB
				+ "' and wb.Waybill_Transaction_Type_NM <> 'LOADING' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";

		String query10 = " select wb.pro_nb,ssst.[Shipment_Service_Sub_Type_NM], ss.[Shipment_Service_Type_NM] from [EQP].[Waybill_vw] wb left join [EQP].[Waybill_Service_vw] wbs on wb.[Pro_NB]=wbs.pro_nb"
				+ " left join [EQP].[Shipment_Service_vw] ss on wbs.[Service_CD]=ss.[Service_CD] left join [EQP].[Shipment_Service_Sub_Type_vw] ssst on ss.[Shipment_Service_Sub_Type_NM]=ssst.[Shipment_Service_Sub_Type_NM]"
				+ " where wb.pro_nb= ? and ss.[Shipment_Service_Type_NM]= ?  order by ssst.[Display_Sequence_NB]  ";

		// Set<ArrayList<String>> d=new HashSet<ArrayList<String>>(); // without
		// sort
		LinkedHashSet<ArrayList<String>> d = new LinkedHashSet<ArrayList<String>>(); // sort
		ResultSet rs = stat.executeQuery(query6);
		PreparedStatement stat2 = conn2.prepareStatement(query10);
		while (rs.next()) {
			String PRONB = rs.getString("pro_nb");
			String pronb = PRONB.substring(0, 3) + "-" + PRONB.substring(3, 9) + "-"
					+ PRONB.substring(9, PRONB.length());
			String ORI = rs.getString("Origin_Facility_CD");
			String DEST = rs.getString("Planned_Destination_Facility_CD");
			String ACTUALW = rs.getString("Total_Actual_Weight_QT");
			// String MANID=rs.getString("Manifest_Destination_Fclty_CD");
			// String HLI=rs.getString("Headload_IN");
			stat2.setString(1, PRONB);
			stat2.setString(2, "flag");
			ResultSet rs5 = stat2.executeQuery();
			ArrayList<String> f = new ArrayList<String>();
			while (rs5.next()) {
				String ServiceName = rs5.getString("Shipment_Service_Sub_Type_NM");
				f.add(ServiceName);
				f.removeAll(Collections.singleton(null));
			}
			String Flag;
			if (f.size() != 0) {
				Flag = f.toString().replaceAll("[\\[\\] ]", "");
			} else {
				Flag = null;
			}
			ArrayList<String> c = new ArrayList<String>();
			c.add(pronb);
			c.add(Flag);
			c.add(ORI);
			c.add(DEST);
			c.add(ACTUALW);
			// c.add(MANID);
			// c.add(HLI);
			c.removeAll(Collections.singleton(null));
			d.add(c);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return d;
	}

	public static ArrayList<ArrayList<Object>> CheckWaybillUpdateForHL(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = conn3.createStatement();
		String query3 = "SELECT  *  FROM  [EQP].[Waybill_vw] wb where  wb.Equipment_Unit_NB='" + TrailerNB
				+ "' and wb.Standard_Carrier_Alpha_CD='" + SCAC
				+ "' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";
		ResultSet rs = stat.executeQuery(query3);
		ArrayList<ArrayList<Object>> pros = new ArrayList<ArrayList<Object>>();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		while (rs.next()) {
			ArrayList<Object> p = new ArrayList<Object>();
			String pro_nb = rs.getString("pro_nb");
			String Headload_IN = rs.getString("Headload_IN");
			Timestamp Waybill_Transaction_End_TS = rs.getTimestamp("Waybill_Transaction_End_TS");
			String Manifest_Destination_Fclty_CD = rs.getString("Manifest_Destination_Fclty_CD");
			String source_modify_ID = rs.getString("source_modify_ID");
			p.add(pro_nb);// 0
			p.add(Headload_IN);// 1
			p.add(Manifest_Destination_Fclty_CD);// 2
			p.add(Waybill_Transaction_End_TS);// 3
			p.add(source_modify_ID);// 4
			pros.add(p);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();

		return pros;
	}

	public static ArrayList<ArrayList<Object>> CheckProNotInLoadingUpdateForHL(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn3.createStatement();
		String query3 = "SELECT  *  FROM  [EQP].[Waybill_vw] wb where  wb.Equipment_Unit_NB='" + TrailerNB
				+ "' and wb.Standard_Carrier_Alpha_CD='" + SCAC
				+ "' and wb.Waybill_Transaction_Type_NM <> 'LOADING' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";
		ResultSet rs = stat.executeQuery(query3);
		ArrayList<ArrayList<Object>> pros = new ArrayList<ArrayList<Object>>();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		while (rs.next()) {
			ArrayList<Object> p = new ArrayList<Object>();
			String pro_nb = rs.getString("pro_nb");
			String Headload_IN = rs.getString("Headload_IN");
			Timestamp Waybill_Transaction_End_TS = rs.getTimestamp("Waybill_Transaction_End_TS");
			String Manifest_Destination_Fclty_CD = rs.getString("Manifest_Destination_Fclty_CD");
			String source_modify_ID = rs.getString("source_modify_ID");
			p.add(pro_nb);// 0
			p.add(Headload_IN);// 1
			p.add(Manifest_Destination_Fclty_CD);// 2
			p.add(Waybill_Transaction_End_TS);// 3
			p.add(source_modify_ID);// 4
			pros.add(p);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();

		return pros;
	}

	public static ArrayList<Object> CheckEquipment(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = null;
		stat = conn3.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		String query3 = "SELECT  *  FROM  EQP.Equipment_vw wb where  wb.Equipment_Unit_NB='" + TrailerNB
				+ "' and wb.Standard_Carrier_Alpha_CD='" + SCAC + "' ";
		ResultSet rs = stat.executeQuery(query3);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		rs.absolute(1);
		ArrayList<Object> EQP = new ArrayList<Object>();
		String Mainframe_User_ID = rs.getString("Mainframe_User_ID");
		String Equipment_Comment_TX = rs.getString("Equipment_Comment_TX");
		EQP.add(Mainframe_User_ID);// 0
		EQP.add(Equipment_Comment_TX);// 2
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();

		return EQP;
	}

	public static ArrayList<Object> GetWaybillInformationOfPro(String ProNb)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat3 = conn4.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		String query4 = " SELECT  * from eqp.waybill_vw where pro_nb='" + ProNb + "' ";
		ArrayList<Object> wb = new ArrayList<Object>();
		ResultSet rs = stat3.executeQuery(query4);
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		rs.absolute(1);
		Timestamp Create_TS = rs.getTimestamp("Create_TS");
		Timestamp Modify_ts = rs.getTimestamp("modify_ts");
		Timestamp System_Insert_TS = rs.getTimestamp("System_Insert_TS");
		Timestamp System_Modify_TS = rs.getTimestamp("System_Modify_TS");
		String Standard_Carrier_Alpha_CD = rs.getString("Standard_Carrier_Alpha_CD");
		String Equipment_Unit_NB = rs.getString("Equipment_Unit_NB");
		String Destination_facility_cd = rs.getString("Destination_facility_cd");
		String Source_Modify_ID = rs.getString("Source_Modify_ID");
		String M204_WGP_TAG_ID = rs.getString("M204_WGP_TAG_ID");
		String M204_SHP_TAG_ID = rs.getString("M204_SHP_TAG_ID");
		String Record_key = rs.getString("Record_key");
		String advance_standard_carr_alpha_cd = rs.getString("Advance_Standard_Carr_Alpha_CD");
		String To_Facility_CD = rs.getString("To_Facility_CD");
		String From_Facility_CD = rs.getString("From_Facility_CD");
		String To_Standard_Carrier_Alpha_CD = rs.getString("To_Standard_Carrier_Alpha_CD");
		String To_Equipment_Unit_NB = rs.getString("To_Equipment_Unit_NB");
		String From_Standard_Carrier_Alpha_CD = rs.getString("From_Standard_Carrier_Alpha_CD");
		String From_Equipment_Unit_NB = rs.getString("From_Equipment_Unit_NB");
		String Waybill_Transaction_Type_NM = rs.getString("Waybill_Transaction_Type_NM");
		String Manifest_Destination_Fclty_CD = rs.getString("Manifest_Destination_Fclty_CD");
		String headload_in = rs.getString("Headload_IN");
		Timestamp Waybill_Transaction_End_TS = rs.getTimestamp("Waybill_Transaction_End_TS");
		String Volume_Exception_IN = rs.getString("Volume_Exception_IN");
		wb.add(Standard_Carrier_Alpha_CD);// 0
		wb.add(Equipment_Unit_NB);// 1
		wb.add(Destination_facility_cd);// 2
		wb.add(Source_Modify_ID);// 3
		wb.add(M204_SHP_TAG_ID);// 4
		wb.add(M204_WGP_TAG_ID);// 5
		wb.add(advance_standard_carr_alpha_cd);// 6
		wb.add(Create_TS);// 7
		wb.add(System_Insert_TS);// 8
		wb.add(System_Modify_TS);// 9
		wb.add(Modify_ts);// 10
		wb.add(Waybill_Transaction_End_TS);// 11
		wb.add(Record_key);// 12
		wb.add(From_Facility_CD);// 13
		wb.add(To_Facility_CD);// 14
		wb.add(From_Standard_Carrier_Alpha_CD);// 15
		wb.add(From_Equipment_Unit_NB);// 16
		wb.add(To_Standard_Carrier_Alpha_CD);// 17
		wb.add(To_Equipment_Unit_NB);// 18
		wb.add(Manifest_Destination_Fclty_CD);// 19
		wb.add(Waybill_Transaction_Type_NM);// 20
		wb.add(headload_in);// 21
		wb.add(Volume_Exception_IN);// 22
		if (rs != null)
			rs.close();
		if (stat3 != null)
			stat3.close();
		if (conn4 != null)
			conn4.close();
		return wb;

	}

	public static ArrayList<Object> CheckEQPStatusUpdate(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = conn3.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
		String query3 = "select top 1 * from [EQP].[Equipment_Status_VW] where Equipment_Unit_NB='" + TrailerNB
				+ "' and Standard_Carrier_Alpha_CD='" + SCAC + "'"
				+ " ORDER BY [Equipment_Status_TS] DESC,[Equipment_Status_System_TS] DESC";
		ResultSet rs = stat.executeQuery(query3);
		ArrayList<Object> status = new ArrayList<Object>();
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		rs.absolute(1);
		String Equipment_Status_Type_CD = rs.getString("Equipment_Status_Type_CD");
		String Statusing_Facility_CD = rs.getString("Statusing_Facility_CD");
		String equipment_dest_facility_cd = rs.getString("equipment_dest_facility_cd");
		String Source_Create_ID = rs.getString("Source_Create_ID");
		String Actual_Capacity_Consumed_PC = rs.getString("Actual_Capacity_Consumed_PC");
		Timestamp Modify_TS = rs.getTimestamp("Modify_TS");
		Timestamp create_ts = rs.getTimestamp("create_ts");
		Timestamp Equipment_Status_TS = rs.getTimestamp("Equipment_Status_TS");
		Timestamp Equipment_Status_System_TS = rs.getTimestamp("Equipment_Status_System_TS");
		String Observed_Shipment_QT = rs.getString("Observed_Shipment_QT");
		String Observed_Weight_QT = rs.getString("Observed_Weight_QT");
		String Seal_NB = rs.getString("Seal_NB");
		String Headload_Capacity_Consumed_PC = rs.getString("Headload_Capacity_Consumed_PC");
		String Headload_Dest_Facility_CD = rs.getString("Headload_Dest_Facility_CD");
		String headload_origin_facility_CD = rs.getString("headload_origin_facility_CD");
		String equipment_origin_facility_CD = rs.getString("equipment_origin_facility_CD");
		String Modify_id = rs.getString("Modify_ID");
		String Mainframe_User_ID = rs.getString("Mainframe_User_ID");
		String M204_Equipment_Status_Type_CD = rs.getString("M204_Equipment_Status_Type_CD");
		String Source_Modify_ID = rs.getString("Source_Modify_ID");
		Timestamp Planned_Delivery_DT = rs.getTimestamp("Planned_Delivery_DT");
		String City_Route_Type_NM = rs.getString("City_Route_Type_NM");
		String City_Route_NM = rs.getString("City_Route_NM");
		status.add(Equipment_Status_Type_CD);// 0
		status.add(Statusing_Facility_CD);// 1
		status.add(equipment_dest_facility_cd);// 2
		status.add(Source_Create_ID);// 3
		status.add(Actual_Capacity_Consumed_PC);// 4
		status.add(Modify_TS);// 5
		status.add(create_ts);// 6
		status.add(Equipment_Status_TS);// 7
		status.add(Equipment_Status_System_TS);// 8
		status.add(Observed_Shipment_QT);// 9
		status.add(Observed_Weight_QT);// 10
		status.add(Seal_NB);// 11
		status.add(Headload_Capacity_Consumed_PC);// 12
		status.add(Headload_Dest_Facility_CD);// 13
		status.add(headload_origin_facility_CD);// 14
		status.add(equipment_origin_facility_CD);// 15
		status.add(Modify_id);// 16
		status.add(Mainframe_User_ID);// 17
		status.add(M204_Equipment_Status_Type_CD);// 18
		status.add(Source_Modify_ID);// 19
		status.add(Planned_Delivery_DT);// 20
		status.add(City_Route_Type_NM);// 21
		status.add(City_Route_NM);// 22
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();

		return status;
	}

	public static ArrayList<String> GetProNotInAnyTrailer() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query3 = " SELECT w.Pro_NB FROM EQP.Waybill_vw w, EQP.Waybill_Service_VW WBS WHERE (w.Shipment_Correction_Type_CD <> 'VO' or w.Shipment_Correction_Type_CD is null)"
				+ " AND ((w.Shipment_Purpose_CD <> 'MR' and  w.Shipment_Purpose_CD <> 'SU') or w.Shipment_Purpose_CD is null) AND w.Equipment_Unit_NB  Is Null  AND w.Standard_Carrier_Alpha_CD is Null"
				+ " AND w.To_Equipment_Unit_NB is Null AND w.To_Standard_Carrier_Alpha_CD is Null AND w.Delivery_DT Is Null AND w.Delivery_TS Is Null AND w.Create_TS > '2015-09-30' and w.pro_nb=wbs.pro_nb and WBS.SERVICE_CD not in ('pois','food') order by NEWID()";
		ArrayList<String> c = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query3);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			c.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return c;
	}

	public static ArrayList<String> GetVolumeProNotInAnyTrailer() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query3 = " SELECT w.Pro_NB FROM EQP.Waybill_vw w, EQP.Waybill_Service_VW WBS WHERE (w.Shipment_Correction_Type_CD <> 'VO' or w.Shipment_Correction_Type_CD is null)"
				+ " AND ((w.Shipment_Purpose_CD <> 'MR' and  w.Shipment_Purpose_CD <> 'SU') or w.Shipment_Purpose_CD is null) AND w.Equipment_Unit_NB  Is Null  AND w.Standard_Carrier_Alpha_CD is Null and w.Total_Actual_Weight_QT > 5000"
				+ " AND w.To_Equipment_Unit_NB is Null AND w.To_Standard_Carrier_Alpha_CD is Null AND w.Delivery_DT Is Null AND w.Delivery_TS Is Null AND w.Create_TS > '2015-09-30' and w.pro_nb=wbs.pro_nb and WBS.SERVICE_CD not in ('pois','food') order by NEWID()";
		ArrayList<String> c = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query3);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			c.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return c;
	}

	public static ArrayList<String> GetProWithType(String SCAC, String TrailerNB, String ProType)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn3 = DataConnection.getConnection();
		Statement stat = conn3.createStatement();
		String query3 = " SELECT WBS.PRO_NB,WBS.SERVICE_CD FROM EQP.Waybill_vw W RIGHT JOIN EQP.Waybill_Service_vw WBS ON W.PRO_NB=WBS.PRO_NB "
				+ " WHERE WBS.SERVICE_CD='" + ProType + "' and (w.Equipment_Unit_NB<>'" + TrailerNB
				+ "' or w.Standard_Carrier_Alpha_CD <>'" + SCAC + "')"
				+ " AND ((w.Shipment_Purpose_CD <> 'MR' and  w.Shipment_Purpose_CD <> 'SU') or w.Shipment_Purpose_CD is null) AND w.Delivery_DT Is Null AND w.Delivery_TS Is Null AND w.Create_TS > '2015-09-30' order by newid() ";
		ArrayList<String> c = new ArrayList<String>();
		ResultSet rs3 = stat.executeQuery(query3);
		while (rs3.next()) {
			String pro = rs3.getString("PRO_NB");
			c.add(pro);
		}
		if (rs3 != null)
			rs3.close();
		if (stat != null)
			stat.close();
		if (conn3 != null)
			conn3.close();

		return c;

	}

	public static ArrayList<String> GetProFromTrailerOnDifferentTerminal(String terminalcd, String SCAC,
			String TrailerNB) throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn2 = DataConnection.getConnection();
		Statement stat = conn2.createStatement();
		String query2 = " select top 50 neqps.[Standard_Carrier_Alpha_CD],neqps.[Equipment_Unit_NB],neqps.[Equipment_Status_Type_CD],neqps.Equipment_Status_TS,neqps.[Statusing_Facility_CD],neqps.Actual_Capacity_Consumed_PC,neqps.Seal_NB,nEqps.Equipment_Dest_Facility_CD,COUNT(wb.pro_nb) AS AmountShip,sum(wb.Total_Actual_Weight_QT) AS AmountWeight,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,wb.pro_nb"
				+ " from  [EQP].[Equipment_vw] eqp,[EQP].[Equipment_Availability_vw] eqpa,[EQP].[Equipment_Status_Type_Transition_vw] eqpst,EQP.Waybill_vw WB,(select [Standard_Carrier_Alpha_CD],[Equipment_Unit_NB],[Equipment_Status_Type_CD],[Statusing_Facility_CD],Equipment_Status_TS,Equipment_Dest_Facility_CD,Actual_Capacity_Consumed_PC,Seal_NB,Headload_Dest_Facility_CD,Headload_Capacity_Consumed_PC,Observed_Shipment_QT,Observed_Weight_QT"
				+ " from (select  eqps.[Standard_Carrier_Alpha_CD],eqps.[Equipment_Unit_NB],eqps.[Equipment_Status_Type_CD],eqps.[Statusing_Facility_CD],eqps.Actual_Capacity_Consumed_PC,eqps.Equipment_Dest_Facility_CD,eqps.Seal_NB,EQPS.Equipment_Status_TS,eqps.Headload_Dest_Facility_CD,eqps.Headload_Capacity_Consumed_PC,eqps.Observed_Shipment_QT,eqps.Observed_Weight_QT,rank() OVER (PARTITION BY [eqps].[Standard_Carrier_Alpha_CD],[eqps].[Equipment_Unit_NB] ORDER BY eqps.Equipment_Status_TS desc, eqps.Equipment_Status_system_TS desc) as num1 from [EQP].[Equipment_Status_vw] eqps) as eqq where eqq.num1=1) Neqps	"
				+ " where neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] and eqp.[Emergency_Repair_Due_IN]='n' and  eqp.Equipment_Type_NM in ('trailer','STRAIGHT TRUCK') and Neqps.Equipment_Dest_Facility_CD is not null"
				+ "  and eqpa.[Standard_Carrier_Alpha_CD]=eqp.[Standard_Carrier_Alpha_CD] and eqpa.[Equipment_Unit_NB]=eqp.[Equipment_Unit_NB]  and neqps.[Equipment_Status_Type_CD]=eqpst.[From_Equipment_Status_Type_CD] and eqpst.[To_Equipment_Status_Type_CD]='ldg' and neqps.[Equipment_Status_Type_CD] in ('ldg','arv','ldd')"
				+ "  and [Equipment_Avbl_Status_NM]='available' and  neqps.[Standard_Carrier_Alpha_CD]=eqpa.[Standard_Carrier_Alpha_CD] and neqps.[Equipment_Unit_NB]=eqpa.[Equipment_Unit_NB] "
				+ "  and eqpa.M204_Occurrence_NB=(Select min(ea1.M204_Occurrence_NB) from EQP.Equipment_Availability_vw ea1  where ea1.Standard_Carrier_Alpha_CD=eqpa.Standard_Carrier_Alpha_CD and ea1.Equipment_unit_NB= eqpa.Equipment_Unit_NB)"
				+ "  and nEqps.Equipment_Unit_NB=wb.Equipment_Unit_NB  and nEqps.Standard_Carrier_Alpha_CD=wb.Standard_Carrier_Alpha_CD "
				+ " and (neqps.Equipment_Unit_NB <>'" + TrailerNB + "' or neqps.Standard_Carrier_Alpha_CD<>'" + SCAC
				+ "') and neqps.Statusing_Facility_CD <>'" + terminalcd
				+ "' group by neqps.Statusing_Facility_CD,neqps.Standard_Carrier_Alpha_CD,neqps.Actual_Capacity_Consumed_PC,neqps.Equipment_Unit_NB,neqps.Equipment_Status_Type_CD,neqps.Equipment_Dest_Facility_CD,neqps.Seal_NB,neqps.Equipment_Status_TS,neqps.Headload_Dest_Facility_CD,neqps.Headload_Capacity_Consumed_PC,neqps.Observed_Shipment_QT,neqps.Observed_Weight_QT,wb.pro_nb "
				+ " order by newid()";
		ArrayList<String> c = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query2);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			c.add(pro);

		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn2 != null)
			conn2.close();

		return c;

	}

	public static ArrayList<String> GetProOnTrailer(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat = conn4.createStatement();
		String query4 = " select wb.pro_nb from eqp.Waybill_vw wb where wb.Standard_Carrier_Alpha_CD ='" + SCAC
				+ "'  and wb.Equipment_Unit_NB='" + TrailerNB
				+ "' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";
		ArrayList<String> d = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query4);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			d.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();

		return d;

	}

	public static ArrayList<String> GetProNotInLoadingOnTrailer(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat = conn4.createStatement();
		String query4 = " select wb.pro_nb from eqp.Waybill_vw wb where wb.Standard_Carrier_Alpha_CD ='" + SCAC
				+ "'  and wb.Equipment_Unit_NB='" + TrailerNB
				+ "' and wb.Waybill_Transaction_Type_NM <> 'LOADING' order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";
		ArrayList<String> d = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query4);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			d.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();

		return d;

	}

	public static ArrayList<String> GetProHasInvalidWeightOnTrailer(String SCAC, String TrailerNB)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat = conn4.createStatement();
		String query4 = " select wb.pro_nb from eqp.Waybill_vw wb where wb.Standard_Carrier_Alpha_CD ='" + SCAC
				+ "'  and wb.Equipment_Unit_NB='" + TrailerNB
				+ "' and (wb.Total_Actual_Weight_QT IN ( 0, 1) or wb.Total_Actual_Weight_QT is null) order by wb.Headload_IN desc,wb.Waybill_Transaction_End_TS,wb.pro_nb";
		ArrayList<String> d = new ArrayList<String>();
		ResultSet rs = stat.executeQuery(query4);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			d.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();

		return d;

	}

	public static ArrayList<String> GenerateProNotInDB() throws InterruptedException {
		ArrayList<String> d = new ArrayList<String>();
		String FirstTWO = "59";
		for (int i = 0; i < 100; i++) {
			SimpleDateFormat sdfDate = new SimpleDateFormat("mmssSSS");
			TimeUnit.MILLISECONDS.sleep(200);
			Date now = new Date();
			String ThreeToNine = sdfDate.format(now);
			String firstNine = FirstTWO + ThreeToNine;
			String CheckDight;
			int remainder = (Integer.parseInt(firstNine.substring(3, 9))) % 11;
			if (remainder == 0) {
				CheckDight = "0";
			} else if (remainder == 1) {
				CheckDight = "X";
			} else {
				CheckDight = Integer.toString(11 - remainder);
			}
			String pro;
			pro = firstNine + CheckDight;
			d.add(pro);
		}
		return d;
	}

	public static ArrayList<String> ProWithDttmsp() throws InterruptedException, ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat = conn4.createStatement();
		String query3 = " UPDATE EQP.Waybill_vw SET M204_WGP_DTTMSP_TS = 0 where Pro_NB LIKE '034%'";
		String query4 = " select wb.pro_nb from eqp.Waybill_vw wb where  wb.M204_WGP_DTTMSP_TS=0 AND ( wb.Shipment_Correction_Type_CD<>'VO' or wb.Shipment_Correction_Type_CD is null) and wb.Delivery_DT Is Null order by newid()";
		ArrayList<String> d = new ArrayList<String>();
		stat.executeUpdate(query3);
		ResultSet rs = stat.executeQuery(query4);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			d.add(pro);
		}

		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();

		return d;

	}

	public static ArrayList<String> GetInbondProOnTrailer(String SCAC, String TrailerNB, String CountryCode)
			throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		Connection conn4 = DataConnection.getConnection();
		Statement stat = conn4.createStatement();

		String query4 = "SELECT w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN FROM EQP.Waybill w , SHIP.Shipment_Characteristic sc  where w.Shipment_Characteristic_KEY = sc.Shipment_Characteristic_KEY "
				+ " AND sc.Shipment_Is_In_Bond_IN = 'Y' " + " AND  W.Equipment_Unit_NB='" + TrailerNB
				+ "' AND W.Standard_Carrier_Alpha_CD='" + SCAC
				+ "' GROUP BY w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN;";

		String query5 = "SELECT w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN,wdc.Delivery_Codeword_CD FROM EQP.Waybill w , SHIP.Shipment_Characteristic sc , EQP.Waybill_Delivery_Codeword wdc where w.Shipment_Characteristic_KEY = sc.Shipment_Characteristic_KEY  AND w.Pro_NB = wdc.Pro_NB "
				+ " AND ( (sc.Shipment_Is_Cntry_Mismatch_IN = 'Y'  AND wdc.Pro_NB not in (Select wdc2.Pro_NB FROM EQP.Waybill_Delivery_Codeword wdc2 Where wdc.Pro_NB = wdc2.Pro_NB AND wdc2.Delivery_Codeword_CD in ('PARS','RCOD','RA49','SZCU','RCSA','CL'))))"
				+ " AND W.Equipment_Unit_NB='" + TrailerNB + "' AND W.Standard_Carrier_Alpha_CD='" + SCAC
				+ "' GROUP BY w.Pro_NB, sc.Shipment_Is_In_Bond_IN, sc.Shipment_Is_Cntry_Mismatch_IN,wdc.Delivery_Codeword_CD;";
		ArrayList<String> d = new ArrayList<String>();
		String query = null;
		if (CountryCode.equalsIgnoreCase("Can")) {
			query = query5;
		} else {
			query = query4;
		}
		ResultSet rs = stat.executeQuery(query);
		while (rs.next()) {
			String pro = rs.getString("PRO_NB");
			d.add(pro);
		}
		if (rs != null)
			rs.close();
		if (stat != null)
			stat.close();
		if (conn4 != null)
			conn4.close();

		return d;

	}

}
