.DEFAULT_GOAL := build_and_run
SRC = sim/src
MATH = $(SRC)/math
UTIL = $(SRC)/util
SIM = $(SRC)/sim
CAR = $(SRC)/car
TSCS = $(SRC)/tscs
SPAWNER = $(SRC)/spawner
TRAVELDATA = $(SRC)/traveldata
MAP = $(SRC)/map
TRACK = $(MAP)/track
INTERSECTION = $(MAP)/intersection
FILES = \
$(CAR)/Car.java \
$(CAR)/CarModel.java \
$(CAR)/CarModelDb.java \
$(INTERSECTION)/Segment.java \
$(INTERSECTION)/Intersection.java \
$(TRACK)/AbstractTrack.java \
$(TRACK)/Bezier2Track.java \
$(TRACK)/LineTrack.java \
$(TRACK)/TrackPosition.java \
$(MATH)/Vector2D.java \
$(MATH)/Pair.java \
$(MATH)/Statistics.java \
$(TSCS)/AbstractTSCS.java \
$(TSCS)/DSCS.java \
$(TSCS)/SAD.java \
$(TSCS)/SADSchedule.java \
$(SPAWNER)/PoissonSpawner.java \
$(SPAWNER)/BinomialSpawner.java \
$(SPAWNER)/SpawnerInterface.java \
$(TRAVELDATA)/TravelData.java \
$(TRAVELDATA)/TravelPlan.java \
$(UTIL)/QuadTree.java \
$(UTIL)/CollisionBox.java \
$(SIM)/Drawable.java \
$(SIM)/EntityDb.java \
$(SIM)/Logic.java \
$(SIM)/SimDisplay.java \
$(SIM)/Const.java \
$(SIM)/Simulation.java

all:
	javac -g $(FILES)
	@echo "---Done!---"

run:
	cd sim/src && java sim.Simulation

build_and_run: all run
