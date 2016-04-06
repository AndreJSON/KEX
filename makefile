.DEFAULT_GOAL := all
SRC = sim/src
MATH = $(SRC)/math
SIM = $(SRC)/sim
VEHICLE = $(SIM)/vehicle
TRACK = $(SIM)/map/track
FILES = \
$(MATH)/Vector2D.java \
$(VEHICLE)/VehicleSpec.java \
$(VEHICLE)/Car.java \
$(TRACK)/TrackPosition.java \
$(TRACK)/AbstractTrack.java \
$(TRACK)/LineTrack.java \
$(TRACK)/SquareCurveTrack.java \
$(SIM)/Simulation.java

all:
	javac -g $(FILES)
	@echo "---Done!---"

run:
	cd sim/src && java sim.Simulation

clean:
	cd sim/src && rm -f sim/vehicle/*.class && rm -f sim/map/track/*.class && rm -f math/*.class