package com.bioxx.tfc2.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IRenderHandler;

public class SkyRenderer extends IRenderHandler
{
	private static final ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
	private static final ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
	private static final ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");

	public SkyRenderer()
	{

	}

	@Override
	public void render(float partialTicks, WorldClient world, Minecraft mc) 
	{
		/*if (mc.world.provider.isSurfaceWorld())
		{
			GlStateManager.disableTexture2D();
			Vec3 vec3 = mc.world.getSkyColor(mc.getRenderViewEntity(), partialTicks);
			float f1 = (float)vec3.xCoord;
			float f2 = (float)vec3.yCoord;
			float f3 = (float)vec3.zCoord;

			GlStateManager.color(f1, f2, f3);
			Tessellator tessellator = Tessellator.getInstance();
			WorldRenderer worldrenderer = tessellator.getWorldRenderer();
			GlStateManager.depthMask(false);
			GlStateManager.enableFog();
			GlStateManager.color(f1, f2, f3);

			if (this.vboEnabled)
			{
				this.skyVBO.bindBuffer();
				GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
				GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
				this.skyVBO.drawArrays(7);
				this.skyVBO.unbindBuffer();
				GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
			}
			else
			{
				GlStateManager.callList(this.glSkyList);
			}

			GlStateManager.disableFog();
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
			RenderHelper.disableStandardItemLighting();
			float[] afloat = mc.world.provider.calcSunriseSunsetColors(mc.world.getCelestialAngle(partialTicks), partialTicks);
			float f7;
			float f8;
			float f9;
			float f10;
			float f11;

			if (afloat != null)
			{
				GlStateManager.disableTexture2D();
				GlStateManager.shadeModel(7425);
				GlStateManager.pushMatrix();
				GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
				GlStateManager.rotate(MathHelper.sin(mc.world.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
				f7 = afloat[0];
				f8 = afloat[1];
				f9 = afloat[2];
				float f12;

				worldrenderer.startDrawing(6);
				worldrenderer.setColorRGBA_F(f7, f8, f9, afloat[3]);
				worldrenderer.addVertex(0.0D, 100.0D, 0.0D);
				boolean flag = true;
				worldrenderer.setColorRGBA_F(afloat[0], afloat[1], afloat[2], 0.0F);

				for (int j = 0; j <= 16; ++j)
				{
					f12 = (float)j * (float)Math.PI * 2.0F / 16.0F;
					float f13 = MathHelper.sin(f12);
					float f14 = MathHelper.cos(f12);
					worldrenderer.addVertex((double)(f13 * 120.0F), (double)(f14 * 120.0F), (double)(-f14 * 40.0F * afloat[3]));
				}

				tessellator.draw();
				GlStateManager.popMatrix();
				GlStateManager.shadeModel(7424);
			}

			GlStateManager.enableTexture2D();
			GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
			GlStateManager.pushMatrix();
			f7 = 1.0F - mc.world.getRainStrength(partialTicks);
			f8 = 0.0F;
			f9 = 0.0F;
			f10 = 0.0F;
			GlStateManager.color(1.0F, 1.0F, 1.0F, f7);
			GlStateManager.translate(0.0F, 0.0F, 0.0F);
			GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
			GlStateManager.rotate(mc.world.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
			f11 = 30.0F;
			mc.renderEngine.bindTexture(locationSunPng);
			worldrenderer.startDrawingQuads();
			worldrenderer.addVertexWithUV((double)(-f11), 100.0D, (double)(-f11), 0.0D, 0.0D);
			worldrenderer.addVertexWithUV((double)f11, 100.0D, (double)(-f11), 1.0D, 0.0D);
			worldrenderer.addVertexWithUV((double)f11, 100.0D, (double)f11, 1.0D, 1.0D);
			worldrenderer.addVertexWithUV((double)(-f11), 100.0D, (double)f11, 0.0D, 1.0D);
			tessellator.draw();
			f11 = 20.0F;
			mc.renderEngine.bindTexture(locationMoonPhasesPng);
			int k = mc.world.getMoonPhase();
			int l = k % 4;
			int i1 = k / 4 % 2;
			float f15 = (float)(l + 0) / 4.0F;
			float f16 = (float)(i1 + 0) / 2.0F;
			float f17 = (float)(l + 1) / 4.0F;
			float f18 = (float)(i1 + 1) / 2.0F;
			worldrenderer.startDrawingQuads();
			worldrenderer.addVertexWithUV((double)(-f11), -100.0D, (double)f11, (double)f17, (double)f18);
			worldrenderer.addVertexWithUV((double)f11, -100.0D, (double)f11, (double)f15, (double)f18);
			worldrenderer.addVertexWithUV((double)f11, -100.0D, (double)(-f11), (double)f15, (double)f16);
			worldrenderer.addVertexWithUV((double)(-f11), -100.0D, (double)(-f11), (double)f17, (double)f16);
			tessellator.draw();
			GlStateManager.disableTexture2D();
			float f19 = mc.world.getStarBrightness(partialTicks) * f7;

			if (f19 > 0.0F)
			{
				GlStateManager.color(f19, f19, f19, f19);

				if (this.vboEnabled)
				{
					this.starVBO.bindBuffer();
					GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
					GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
					this.starVBO.drawArrays(7);
					this.starVBO.unbindBuffer();
					GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
				}
				else
				{
					GlStateManager.callList(this.starGLCallList);
				}
			}

			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.disableBlend();
			GlStateManager.enableAlpha();
			GlStateManager.enableFog();
			GlStateManager.popMatrix();
			GlStateManager.disableTexture2D();
			GlStateManager.color(0.0F, 0.0F, 0.0F);
			double d0 = mc.player.getPositionEyes(partialTicks).yCoord - mc.world.getHorizon();

			if (d0 < 0.0D)
			{
				GlStateManager.pushMatrix();
				GlStateManager.translate(0.0F, 12.0F, 0.0F);

				if (this.vboEnabled)
				{
					this.sky2VBO.bindBuffer();
					GL11.glEnableClientState(GL11.GL_VERTEX_ARRAY);
					GL11.glVertexPointer(3, GL11.GL_FLOAT, 12, 0L);
					this.sky2VBO.drawArrays(7);
					this.sky2VBO.unbindBuffer();
					GL11.glDisableClientState(GL11.GL_VERTEX_ARRAY);
				}
				else
				{
					GlStateManager.callList(this.glSkyList2);
				}

				GlStateManager.popMatrix();
				f9 = 1.0F;
				f10 = -((float)(d0 + 65.0D));
				f11 = -1.0F;
				worldrenderer.startDrawingQuads();
				worldrenderer.setColorRGBA_I(0, 255);
				worldrenderer.addVertex(-1.0D, (double)f10, 1.0D);
				worldrenderer.addVertex(1.0D, (double)f10, 1.0D);
				worldrenderer.addVertex(1.0D, -1.0D, 1.0D);
				worldrenderer.addVertex(-1.0D, -1.0D, 1.0D);
				worldrenderer.addVertex(-1.0D, -1.0D, -1.0D);
				worldrenderer.addVertex(1.0D, -1.0D, -1.0D);
				worldrenderer.addVertex(1.0D, (double)f10, -1.0D);
				worldrenderer.addVertex(-1.0D, (double)f10, -1.0D);
				worldrenderer.addVertex(1.0D, -1.0D, -1.0D);
				worldrenderer.addVertex(1.0D, -1.0D, 1.0D);
				worldrenderer.addVertex(1.0D, (double)f10, 1.0D);
				worldrenderer.addVertex(1.0D, (double)f10, -1.0D);
				worldrenderer.addVertex(-1.0D, (double)f10, -1.0D);
				worldrenderer.addVertex(-1.0D, (double)f10, 1.0D);
				worldrenderer.addVertex(-1.0D, -1.0D, 1.0D);
				worldrenderer.addVertex(-1.0D, -1.0D, -1.0D);
				worldrenderer.addVertex(-1.0D, -1.0D, -1.0D);
				worldrenderer.addVertex(-1.0D, -1.0D, 1.0D);
				worldrenderer.addVertex(1.0D, -1.0D, 1.0D);
				worldrenderer.addVertex(1.0D, -1.0D, -1.0D);
				tessellator.draw();
			}

			if (mc.world.provider.isSkyColored())
			{
				GlStateManager.color(f1 * 0.2F + 0.04F, f2 * 0.2F + 0.04F, f3 * 0.6F + 0.1F);
			}
			else
			{
				GlStateManager.color(f1, f2, f3);
			}

			GlStateManager.pushMatrix();
			GlStateManager.translate(0.0F, -((float)(d0 - 16.0D)), 0.0F);
			GlStateManager.callList(this.glSkyList2);
			GlStateManager.popMatrix();
			GlStateManager.enableTexture2D();
			GlStateManager.depthMask(true);
		}*/
	}

}
