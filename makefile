BASE = sim/src/sim
VEHICLE = $(BASE)/vehicle
TRACK = $(BASE)/map/track
FILES = \
$(VEHICLE)/VehicleSpec.java \
$(TRACK)/Vector2D.java \
$(TRACK)/TrackPosition.java \
$(TRACK)/AbstractTrack.java \
$(TRACK)/LineTrack.java \
$(TRACK)/SquareCurveTrack.java

default:
	javac -g $(FILES)

run:
	cd sim/src && java sim.map.track.SquareCurveTrack

clean:
	cd sim/src && rm -f sim/vehicle/*.class && rm -f sim/map/track/*.class
