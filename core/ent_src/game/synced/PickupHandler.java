package game.synced;
import java.util.ArrayList;

import basic.InputPacketProcessor;
import basic.OutputPacketProcessor;
import basic.Rectangle;
import basic.Vector;
import game.entity.Pickup;
import handlers.basic.PacketFieldInfo;
import handlers.basic.PacketFieldInfo.Interpolation;
import handlers.basic.entity.SyncedGameEntity;
import handlers.basic.packets.EntityState;
import render.RenderEntityMainHandler;
import render.SpriteHandler;
import simulation.SimulationCoordinator;
import simulation.SimulationHandler;
import simulation.SyncedEntityHandler;
import simulation.handlers.interaction.EntityCollisionHandler;

class PickupState extends EntityState
{
	public String type;
	PickupState() {
	}
}


public class PickupHandler extends SyncedEntityHandler<PickupState,Pickup>
{
	EntityCollisionHandler ColHandle;
	RenderEntityMainHandler RendEntMainHandle;
	SpriteHandler SH;
	ArrayList <Integer> pickupTypes = new ArrayList<Integer>();
	
	public PickupHandler(SimulationCoordinator SimCoord) {
		super(SimCoord);
		this.ColHandle = SimCoord.SimHandle.EntColHandle;
		this.RendEntMainHandle = SimCoord.SimHandle.RendEntMainHandle;
		this.SH = this.RendEntMainHandle.SpriteHandle;
	}
	
	public int create_pickup_type(String sprite_name) {
		pickupTypes.add(RendEntMainHandle.TexIDManage.get_sprite_id(sprite_name));
		return pickupTypes.size()-1;
	}
	
	@Override
	protected String get_params_from_packet(PickupState ent_packet) {
		Object[] params = new Object[2];
		PickupState pickup_packet = (PickupState)ent_packet;
		params[0] = pickup_packet.type;
		params[1] = new Vector(pickup_packet.pos);
		return ""+pickup_packet.type+",pickup_health";
	}
	
	public Pickup create_new_element() {
		return new Pickup();
	}

	@Override
	public Pickup add_resource_object(Vector pos, Rectangle bound, String params) {
		Pickup pickup = super.add_resource_object(pos, bound, params);
		SpriteHandler SH = RendEntMainHandle.SpriteHandle;
		String token[] = params.split(",");
//		int type = Integer.parseInt(token[0]);
		String sprite_name = "pickup_health";
		if(token.length > 0) {
			pickup.type = token[0];
		}
		if(token.length > 1)
			sprite_name = token[1];
		pickup.bound.set(0,0,8,8);

		pickup.moveObj = ColHandle.add_resource_object(pickup.pos,pickup.bound,null);
		pickup.moveObj.set_z_pos((byte)0);
		pickup.moveObj.set_z_height((byte)2);
		pickup.moveObj.set_solid_flag(false);
//		pickup.moveObj.myEntity=pickup;

		pickup.sprite = SH.add_resource_object(pickup.pos,pickup.bound,null);
		pickup.sprite.set_angle(pickup.get_angle());
		pickup.sprite.set_sprite_type(RendEntMainHandle.TexIDManage.get_sprite_id(sprite_name));
		pickup.sprite.set_color(0.2f,0.2f,0.2f,0.2f);
		return pickup;
	}

	@Override
	protected void client_handle_entity(Pickup pickup)  {
		pickup.sprite.set_color(0.2f,0.2f,0.2f,0.2f);
		pickup.sprite.set(pickup.pos, pickup.bound,pickup.get_angle(),pickup.sprite.get_sprite_type());
		pickup.moveObj.pos.set(pickup.pos);
		pickup.moveObj.bound.set(pickup.bound);
		PickupState ent_update = (PickupState)pickup.serverOutput.get_last_element();
		if(ent_update != null) {
			read_entity_state(ent_update,pickup);
		}
	}

	@Override
	protected void server_handle_entity(Pickup pickup) {
//		pickup.moveObj.bound.set(pickup.bound);
//		if(pickup.moveObj.hit_entity instanceof SyncedGameEntity) {
//			pickup.mark_disable();
//		}
//		ColHandle.check_collision_move_objects(pickup.moveObj);
	}

	@Override
	public Class[] init_packets() {
		Class clazz[] = new Class[2];
		clazz[0] = PickupState.class;
		clazz[1] = PickupState[].class;
 		return clazz;
	}

	@Override
	public void write_entity_state(PickupState ent_packet, Pickup ent) {
//		super.server_write_packet(ent_packet,ent);
//		PickupState pickup_packet = (PickupState)ent_packet;
//		pickup_packet.type = ent.type;
	}

	@Override
	public void read_entity_state(PickupState ent_packet, Pickup ent) {
//		super.client_read_packet(ent_packet,ent);
//		ent.type = pickup_packet.type;
	}
	
	@Override
	public PacketFieldInfo[] get_packet_field_info() {
		return new PacketFieldInfo[] {
				new PacketFieldInfo("pos",'v',PacketFieldInfo.BASELINE,Interpolation.INTERPOLATE),
				new PacketFieldInfo("angle",'f',PacketFieldInfo.BASELINE,Interpolation.ANGLE),
				new PacketFieldInfo("state",'b',PacketFieldInfo.ON_CHANGE,Interpolation.PREVIOUS),
				new PacketFieldInfo("type",'b',PacketFieldInfo.ON_CHANGE,Interpolation.PREVIOUS),
		};
	}

	@Override
	public void get_packet_field(PickupState act_packet,OutputPacketProcessor output, int id) {
		if(id==0) output.write_vector(act_packet.pos);
		if(id==2) output.write_float(act_packet.angle);
		if(id==3) output.write_byte(act_packet.state);
		if(id==4) output.write_string(act_packet.type);
	}
	
	@Override
	public void set_packet_field(PickupState act_packet,InputPacketProcessor input, int id) {
		if(id==0) input.read_vector(act_packet.pos);
		if(id==2) act_packet.angle = input.read_float();
		if(id==3) act_packet.state = input.read_byte();
		if(id==4) act_packet.type = input.read_string();
	}
	
	@Override
	public void copy_packet(PickupState pickup_packet, PickupState src_packet ) {
		pickup_packet.pos.set(src_packet.pos);
		pickup_packet.angle = src_packet.angle;
		pickup_packet.state = src_packet.state;
		pickup_packet.type = src_packet.type;
	}

	@Override
	public PickupState create_entity_state_raw() {
		return  new PickupState();
	}

	@Override
	public void modify_state_send_fields(PickupState epacket) {
		
	}
}

