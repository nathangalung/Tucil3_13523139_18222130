# Rush Hour Game Solver

A Rush Hour puzzle solver using the A* algorithm.

## Team Members
- Jonathan Kenan Budianto (13523139)
- Bryan P. Hutagalung (18222130)

## Description

This program implements a solver for the Rush Hour puzzle game using the A* search algorithm. In Rush Hour, the player must move vehicles (represented by letters) on a grid to allow the primary vehicle (usually represented by 'P') to exit through a designated opening.

The solver supports:
- Multiple heuristic functions for A* search
- Interactive GUI for visualization
- CLI interface for automated solving
- Custom puzzle input through text files

## Features

- **A* Path Finding**: Efficiently solves Rush Hour puzzles using the A* algorithm with custom heuristics
- **Interactive Mode**: GUI visualization showing the step-by-step solution
- **Multiple Puzzle Support**: Reads and solves puzzles from text files
- **Solution Statistics**: Provides metrics like number of moves, nodes explored, and execution time

## Installation and Setup

### Requirements
- Java JDK 11 or higher
- Maven (for building)

### Building the Project
```bash
# Clone the repository
git clone https://github.com/nathangalung/Tucil3_13523139_18222130.git

# Navigate to the project directory
cd Tucil3_13523139_18222130

# Compile the project
javac -d bin src/Main.java src/algorithm/*.java src/core/*.java src/heuristic/*.java src/ui/*.java

# Run using GUI
java -cp bin Main

# Run using CLI
java -cp bin Main --cli