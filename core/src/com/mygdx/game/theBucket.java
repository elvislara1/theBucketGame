package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import jdk.tools.jaotc.ELFMacroAssembler;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class theBucket extends ApplicationAdapter {
	Random random;
	SpriteBatch batch;
	BitmapFont font;
	Texture bucket, gota, fondo, start, gameOver;

	// Sonidos //
	Sound dropWater;
	Sound gOver;
	Sound fallo;
	Music rainMusic;

	int puntuacion = 0;
	int vidas = 5;

	//Objetos
	float bucketX;
	float bucketY;

	//Array con dos gotas
	float[] gX = new float[2];
	float[] gY = new float[2];

	float time;

	int pantalla = 0;
	static final int VELOCIDAD_NIVEL_1 = 5;
	static final int VELOCIDAD_NIVEL_2 = 9;
	static final int VELOCIDAD_NIVEL_3 = 13;

	//Create
	@Override
	public void create() {
		random = new Random();
		batch = new SpriteBatch();

		//fuenteLetra
		font = new BitmapFont(Gdx.files.internal("letra.fnt"), Gdx.files.internal("letra.png"),false);

		//cargamos dropwater efecto y rainbackground music
		dropWater = Gdx.audio.newSound(Gdx.files.internal("dropWater.mp3"));
		fallo = Gdx.audio.newSound(Gdx.files.internal("fallo.mp3"));
		gOver = Gdx.audio.newSound(Gdx.files.internal("gOver.mp3"));
		rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rainMusic.mp3"));
		//texturas
		bucket = new Texture("bucket.png");
		gota = new Texture("gota.png");
		fondo = new Texture("fondo.jpg");
		start = new Texture("fondostart2.png");
		gameOver = new Texture("gameover.png");

		restartGame();
	}
	void restartGame(){
		//positionBucket
		bucketX = 300;
		bucketY = 10;
		//positionGota
		for (int i = 0; i < gY.length; i++) {
			if (i == 1){
				gY[i] = 910 + 150;
			} else {
				gY[i] = 910;
			}
			for (int j = 0; j < gX.length; j++) {
				gX[j] = random.nextInt(440);
			}
		}

		//vidas
		vidas = 5;
		//puntuacion
		puntuacion = 0;
		//tiempo
		time = 0;
		//sound
		fallo.pause();
	}

	//MainLoop
	@Override
	public void render() {

		if (pantalla == 1) {
			//Empieza la lluvia
			rainMusic.play();
			//tiempo
			time += Gdx.graphics.getDeltaTime();

			//borrar la pantalla
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			//dibujoFondo
			batch.begin();
			batch.draw(fondo, 0, 0, 670, 480);
			batch.end();

			if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
				bucketX += 8;
			} else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
				bucketX -= 8;
			}

			for (int i = 0; i < gY.length; i ++){
				//velocidad
				if (puntuacion <= 5){
					gY[i] -= VELOCIDAD_NIVEL_1;
				} else if (puntuacion >= 5 && puntuacion < 10){
					gY[i] -= VELOCIDAD_NIVEL_2;
				} else if (puntuacion >= 10){
					gY[i] -= VELOCIDAD_NIVEL_3;
				}

				//Comprobar SI la GOTA colisiona con el BUCKET
				if (gY[i] <= bucketY + 58 && (gX[i] <= bucketX + 50 && gX[i] >= bucketX - 20)) {
					System.out.println("HA TOCADO LA GOTA");
					//Vuelve a la pos 800
					gY[i] = 800;
					gX[i] = random.nextInt(460);
					puntuacion++;
					dropWater.play();
				}
				// Si la gota SALE de la pantalla, VUELVE a la pos 800Y y restamos una vida.
				if (gY[i] < -60) {
					//Vuelve a la pos 800Y
					gY[i] = 800;
					gX[i] = random.nextInt(460);
					//Restamos la vida + efecto de sonido
					vidas--;
					fallo.play();
				} else if (vidas <= 0){
					pantalla = 2;
					fallo.stop();
					gOver.play();
				}
			}

			//Mantener el BUCKET dentro de los limites de la pantalla.
			if (bucketX < 0){
				bucketX = 0;
			} else if (bucketX > 650 - 60){
				bucketX = 650 - 60;
			}

			//Dibujar
			batch.begin();
			batch.draw(bucket, bucketX, bucketY);
			for(int i = 0; i < gY.length; i++) {
				batch.draw(gota, gX[i], gY[i]);
			}
			font.setColor(Color.WHITE);
			font.draw(batch, "Score: " + Integer.toString(puntuacion), 280, 465);
			if (vidas == 1){
				font.setColor(Color.BLACK);
				font.draw(batch, "NO TE QUEDAN MAS VIDAS", 170, 434);
				font.setColor(Color.RED);
				font.draw(batch, "NO TE QUEDAN MAS VIDAS", 170, 435);
			} else {
				font.draw(batch, "Vidas: " + Integer.toString(vidas), 280, 435);
			}
			if (puntuacion <= 6){
				font.setColor(Color.GREEN);
				font.draw(batch, "LEVEL 1", 500, 107);
			} else if (puntuacion >= 6 & puntuacion < 10){
				font.setColor(Color.ORANGE);
				font.draw(batch, "LEVEL 2", 500, 107);
			} else if (puntuacion >= 10){
				font.setColor(Color.BLACK);
				font.draw(batch, "EXPERT", 500, 106);
				font.setColor(Color.RED);
				font.draw(batch, "EXPERT", 500, 107);
			}
			batch.end();

		} else if (pantalla == 0){
			//PantallaStart
			if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)){
				pantalla = 1;
			}

			//drawStart
			batch.begin();
			batch.draw(start,0,0,645,480);
			batch.end();
		} else {
			//PantallaGameOver
			rainMusic.stop();

			if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
				pantalla = 1;
				restartGame();
			}

			batch.begin();
			batch.draw(gameOver,0,0,645,480);
			font.setColor(Color.WHITE);
			font.draw(batch, "Time: " + String.format("%5.2f",time), 45, 105);
			font.draw(batch, "Score: " + Integer.toString(puntuacion), 45, 75);
			batch.end();
		}
	}
}


