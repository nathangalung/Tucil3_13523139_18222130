# Rush Hour Game Solver

A Rush Hour puzzle solver using the pathfinding algorithm and heuristic.

## Team Members
- Jonathan Kenan Budianto (13523139)
- Bryan P. Hutagalung (18222130)

## Description

This program implements a solver for the Rush Hour puzzle game using the pathfinding algorithm and heuristic. In Rush Hour, the player must move vehicles (represented by letters) on a grid to allow the primary vehicle (usually represented by 'P') to exit through a designated opening.

The solver supports:
- Multiple algorithm and heuristic functions
- Interactive GUI for visualization
- CLI interface for automated solving
- Custom puzzle input through text files

## Features

- **Path Finding Algorithm and Heuristics**: Efficiently solves Rush Hour puzzles using many pathfinding algorithm with custom heuristics
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