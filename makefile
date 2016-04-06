.DEFAULT_GOAL := all
BASE = sim/src/sim
VEHICLE = $(BASE)/vehicle
TRACK = $(BASE)/map/track
FILES = \
$(VEHICLE)/VehicleSpec.java \
$(TRACK)/Vector2D.java \
$(TRACK)/TrackPosition.java \
$(TRACK)/AbstractTrack.java \
$(TRACK)/LineTrack.java \
$(TRACK)/SquareCurveTrack.java \
$(BASE)/Simulation.java

all:
	javac -g $(FILES)
	@echo "---Done!---"

run:
	cd sim/src && java sim.Simulation

clean:
	cd sim/src && rm -f sim/vehicle/*.class && rm -f sim/map/track/*.class
