.DEFAULT_GOAL := build_and_run
SRC = sim/src
MATH = $(SRC)/math
SIM = $(SRC)/sim
CAR = $(SRC)/car
MAP = $(SRC)/map
TRACK = $(MAP)/track
INTERSECTION = $(MAP)/intersection
FILES = \
$(CAR)/Car.java \
$(CAR)/CarModel.java \
$(CAR)/CarModelDatabase.java \
$(INTERSECTION)/Segment.java \
$(INTERSECTION)/Intersection.java \
$(TRACK)/AbstractTrack.java \
$(TRACK)/Bezier2Track.java \
$(TRACK)/LineTrack.java \
$(TRACK)/TrackPosition.java \
$(MATH)/Vector2D.java \
$(SIM)/Drawable.java \
$(SIM)/EntityDatabase.java \
$(SIM)/Logic.java \
$(SIM)/SimDisplay.java \
$(SIM)/TravelData.java \
$(SIM)/Simulation.java

all:
	javac -g $(FILES)
	@echo "---Done!---"

run:
	cd sim/src && java sim.Simulation

build_and_run: all run
