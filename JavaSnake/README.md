## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Installation](#installation)
- [Usage](#usage)
- [Controls](#controls)

## Introduction
This is a simple java snake game except everytime the snake eats the apple, its head and tail will swap.

## Features
- Move the snake in four directions using arrow keys (up, down, left, right).
- Pause and resume the game using space bar
- Snake speeds up after eating each apple until a set maximum.
- Swap the snake's head and tail after eating an apple.
- Apples do not spawn on the snake's body.
- Two consecutive turning command (such as turnleft + turnleft) within the same snake frame will be declined, thus preventing pressing 2 arrow keys at the same time causing suicide.
- Game Over screen with the score display.

## Installation 
- Navigate to project directory
```bash
cd snake-game
```
- Compile Java source files.
```bash
javac GamePanel.java
javac GameFrame.java
```
- Run game.
```bash
java SnakeGame.java
```

## Usage
- Lauch the game with the procedure above
- Navigate the snake with arrowkeys to eat the apple
- Press spacebar to pause/resume
- When snake hits the border or its own body, the game ends

## Controls
- Arrowkeys (up; down; left; right) to navigate
- spacebar to pause


