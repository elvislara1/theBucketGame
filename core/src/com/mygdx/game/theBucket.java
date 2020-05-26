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

	int puntuacion = 0;
	int vidas = 5;

	Cubo cubo = new Cubo(300,10);
	Gotas gotas = new Gotas();

	float time;

	int pantalla = 0;

	//Create
	@Override
	public void create() {
		random = new Random();
		batch = new SpriteBatch();

		//fuenteLetra
		font = new BitmapFont(Gdx.files.internal("letra.fnt"), Gdx.files.internal("letra.png"),false);

		//cargamos los efectos de sonido
		Recursos.dropWater = Gdx.audio.newSound(Gdx.files.internal("dropWater.mp3"));
		Recursos.fallo = Gdx.audio.newSound(Gdx.files.internal("fallo.mp3"));
		Recursos.gOver = Gdx.audio.newSound(Gdx.files.internal("gOver.mp3"));
		Recursos.rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rainMusic.mp3"));
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
		cubo.bucketX = 300;
		cubo.bucketY = 10;
		//positionGota
		for (int i = 0; i < gotas.gY.length; i++) {
			if (i == 1){
				gotas.gY[i] = 910 + 150;
			} else {
				gotas.gY[i] = 910;
			}
			for (int j = 0; j < gotas.gX.length; j++) {
				gotas.gX[j] = random.nextInt(440);
			}
		}

		//vidas
		vidas = 5;
		//puntuacion
		puntuacion = 0;
		//tiempo
		time = 0;
		//sound
		Recursos.fallo.pause();
	}

	//MainLoop
	@Override
	public void render() {

		if (pantalla == 1) {
			//empieza la lluvia
			Recursos.rainMusic.play();
			//tiempo
			time += Gdx.graphics.getDeltaTime();

			//borrar la pantalla
			Gdx.gl.glClearColor(0, 0, 0, 1);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			//drawFondo
			batch.begin();
			batch.draw(fondo, 0, 0, 670, 480);
			batch.end();

			if (Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)) {
				cubo.bucketX += 8;
			} else if (Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)) {
				cubo.bucketX -= 8;
			}

			for (int i = 0; i < gotas.gY.length; i ++){
				//velocidad
				if (puntuacion <= 5){
					gotas.gY[i] -= Const.VELOCIDAD_NIVEL_1;
				} else if (puntuacion >= 5 && puntuacion < 10){
					gotas.gY[i] -= Const.VELOCIDAD_NIVEL_2;
				} else if (puntuacion >= 10){
					gotas.gY[i] -= Const.VELOCIDAD_NIVEL_3;
				}

				//Comprobar SI la GOTA colisiona con el BUCKET
				if (gotas.gY[i] <= cubo.bucketY + 58 && (gotas.gX[i] <= cubo.bucketX + 50 && gotas.gX[i] >= cubo.bucketX - 20)) {
					System.out.println("HA TOCADO LA GOTA");
					//Vuelve a la pos 800
					gotas.gY[i] = 800;
					gotas.gX[i] = random.nextInt(460);
					puntuacion++;
					Recursos.dropWater.play();
				}
				// Si la gota SALE de la pantalla, VUELVE a la pos 800Y y restamos una vida.
				if (gotas.gY[i] < -60) {
					//Vuelve a la pos 800Y
					gotas.gY[i] = 800;
					gotas.gX[i] = random.nextInt(460);
					vidas--;
					Recursos.fallo.play();
				} else if (vidas <= 0){
					pantalla = 2;
					Recursos.fallo.stop();
					Recursos.gOver.play();
				}
			}

			//Mantener el BUCKET dentro de los limites de la pantalla.
			if (cubo.bucketX < 0){
				cubo.bucketX = 0;
			} else if (cubo.bucketX > 650 - 60){
				cubo.bucketX = 650 - 60;
			}

			//draw bucket y gotas
			batch.begin();
			batch.draw(bucket, cubo.bucketX, cubo.bucketY);
			for(int i = 0; i < gotas.gY.length; i++) {
				batch.draw(gota, gotas.gX[i], gotas.gY[i]);
			}
			//Durante el juego habrá modificaciónes como: mostrará el score, vidas y en el nivel en el que encuentra.
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
			//Nivel
			if (puntuacion <= 6){
				font.setColor(Color.GREEN);
				font.draw(batch, "LEVEL 1", 500, 107);
			} else if (puntuacion >= 6 & puntuacion < 10){
				font.setColor(Color.ORANGE);
				font.draw(batch, "LEVEL 2", 500, 107);
			} else if (puntuacion >= 10){
				//Sombras en el texto, para una mejor visualización
				font.setColor(Color.BLACK);
				font.draw(batch, "EXPERT", 500, 106);
				font.setColor(Color.RED);
				font.draw(batch, "EXPERT", 500, 107);
			}
			batch.end();

		} else if (pantalla == 0){
			//pantallaStart
			if (Gdx.input.isKeyPressed(Input.Keys.ANY_KEY)){
				pantalla = 1;
			}

			batch.begin();
			batch.draw(start,0,0,645,480);
			batch.end();
		} else {
			//pantallaGameOver
			Recursos.rainMusic.stop();

			if (Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
				pantalla = 1;
				restartGame();
			}
			//Al final del juego, mostrará en pantalla los puntos conseguidos, y el tiempo de vida del jugador
			batch.begin();
			batch.draw(gameOver,0,0,645,480);
			font.setColor(Color.WHITE);
			font.draw(batch, "Time: " + String.format("%5.2f",time), 45, 105);
			font.draw(batch, "Score: " + Integer.toString(puntuacion), 45, 75);
			batch.end();
		}
	}
}
