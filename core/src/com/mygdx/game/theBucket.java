package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class theBucket extends ApplicationAdapter {

	Random random;
	SpriteBatch batch;

	BitmapFont font;

	int puntuacion = 0;
	int vidas = 5;

	Texture bucket, gota, fondo, start, gameOver;

	//Objetos
	float bucketX;
	float bucketY;

	float gotaX, gotaY;
	float gota2X, gota2Y;

	//MEJORA, por cada array gotaX, gotaY [2]
/*
	float[] gX = new float[2];
	float[] gY = new float[2];
*/

	float time;

	int pantalla = 0;
	int veces = 0;

	boolean chocado;

	//Create
	@Override
	public void create() {
		random = new Random();
		batch = new SpriteBatch();

		//fuenteLetra
		font = new BitmapFont(Gdx.files.internal("letra.fnt"), Gdx.files.internal("letra.png"),false);

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
			gotaY = 600;
			gotaX = random.nextInt(400);
			gota2Y = gotaY + 300;
			gota2X = random.nextInt(400);
		//vidas
			vidas = 5;
		//puntuacion
			puntuacion = 0;
		//tiempo
			time = 0;
	}

	//MainLoop
	@Override
	public void render() {
		if (pantalla == 1) {
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

			//velocidad
			gotaY -= 5;
			if (puntuacion >= 3 || puntuacion >= 10){
				gotaY -= 4;
			}
			//Comprobar TOCADO(pruebas)

			if (gotaY <= bucketY + 58 && (gotaX <= bucketX + 20 && gotaX >= bucketX - 20)) {
					//pruebas
					System.out.println("TOCADO");
					System.out.println("esto es Gota : " + gotaX + "   " + gotaY);
					System.out.println("esto es Bucket : " + bucketX + "   " + bucketY);
					drawAgain();
					puntuacion++;
			 		chocado = true;
			}

			// Si la gota sale de la pantalla, vuelve a la pos 430Y y restamos una vida.
			if (gotaY < -60) {
				drawAgain();
				vidas--;
			} else if (vidas == 0){
				pantalla = 2;
			}

			//Mantener el bucket dentro de los limites de la pantalla.
			if (bucketX < 0){
				bucketX = 0;
			} else if (bucketX > 650 - 60){
				bucketX = 650 - 60;
			}

			//dibujar
			batch.begin();
			batch.draw(bucket, bucketX, bucketY);
			batch.draw(gota, gotaX, gotaY);
			font.setColor(Color.WHITE);
			font.draw(batch, "Score: " + Integer.toString(puntuacion), 280, 465);
			if (vidas == 1){
				font.setColor(Color.RED);
				font.draw(batch, "NO TE QUEDAN MAS VIDAS", 170, 435);
			} else {
				font.draw(batch, "Vidas: " + Integer.toString(vidas), 280, 435);
			}
			batch.end();
			veces++;

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
	public void drawAgain(){
		gotaY = 430;
		gotaX = random.nextInt(400);
	}
}

