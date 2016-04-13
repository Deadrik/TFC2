package com.bioxx.tfc2.entity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

import com.bioxx.tfc2.api.types.Gender;
import com.bioxx.tfc2.entity.EntityBear.BearType;
import com.bioxx.tfc2.entity.EntityBoar.BoarStage;
import com.bioxx.tfc2.entity.EntityElephant.ElephantType;
import com.bioxx.tfc2.entity.EntityTiger.TigerType;

public class DataSerializersTFC 
{
	public static final DataSerializer<Gender> GENDER = new DataSerializer<Gender>(){

		@Override
		public Gender read(PacketBuffer buf) {
			return (Gender)buf.readEnumValue(Gender.class);
		}


		@Override
		public DataParameter<Gender> createKey(int id) {
			return new DataParameter(id, this);
		}


		@Override
		public void write(PacketBuffer buf, Gender value) {
			buf.writeEnumValue(value);
		}
	};

	public static final DataSerializer<TigerType> TIGERTYPE = new DataSerializer<TigerType>(){

		@Override
		public TigerType read(PacketBuffer buf) {
			return (TigerType)buf.readEnumValue(TigerType.class);
		}


		@Override
		public DataParameter<TigerType> createKey(int id) {
			return new DataParameter(id, this);
		}


		@Override
		public void write(PacketBuffer buf, TigerType value) {
			buf.writeEnumValue(value);
		}
	};

	public static final DataSerializer<BearType> BEARTYPE = new DataSerializer<BearType>(){

		@Override
		public BearType read(PacketBuffer buf) {
			return (BearType)buf.readEnumValue(BearType.class);
		}


		@Override
		public DataParameter<BearType> createKey(int id) {
			return new DataParameter(id, this);
		}


		@Override
		public void write(PacketBuffer buf, BearType value) {
			buf.writeEnumValue(value);
		}
	};

	public static final DataSerializer<ElephantType> ELEPHANTTYPE = new DataSerializer<ElephantType>(){

		@Override
		public ElephantType read(PacketBuffer buf) {
			return (ElephantType)buf.readEnumValue(ElephantType.class);
		}


		@Override
		public DataParameter<ElephantType> createKey(int id) {
			return new DataParameter(id, this);
		}


		@Override
		public void write(PacketBuffer buf, ElephantType value) {
			buf.writeEnumValue(value);
		}
	};

	public static final DataSerializer<BoarStage> BOARTYPE = new DataSerializer<BoarStage>(){

		@Override
		public BoarStage read(PacketBuffer buf) {
			return (BoarStage)buf.readEnumValue(BoarStage.class);
		}


		@Override
		public DataParameter<BoarStage> createKey(int id) {
			return new DataParameter(id, this);
		}


		@Override
		public void write(PacketBuffer buf, BoarStage value) {
			buf.writeEnumValue(value);
		}
	};

	public static void register()
	{
		DataSerializers.registerSerializer(GENDER);
		DataSerializers.registerSerializer(TIGERTYPE);
		DataSerializers.registerSerializer(BEARTYPE);
		DataSerializers.registerSerializer(ELEPHANTTYPE);
		DataSerializers.registerSerializer(BOARTYPE);
	}
}
