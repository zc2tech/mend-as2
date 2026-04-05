#!/bin/bash
# SSH Tunnel to CloudFoundry PostgreSQL
# This script creates an SSH tunnel through the CloudFoundry app container
# to access the PostgreSQL service from your local machine

set -e

echo "Creating SSH tunnel to CloudFoundry PostgreSQL..."
echo "This will map local port 5432 to the CloudFoundry PostgreSQL service"
echo ""
echo "Once the tunnel is established, you can connect from another terminal with:"
echo "  psql -h localhost -U 245a50905e66 -d BUIOHWYdgYnp -p 5432"
echo ""
echo "Press Ctrl+C to close the tunnel when done"
echo ""

# Create SSH tunnel through CloudFoundry
# Maps local port 5432 to the remote PostgreSQL host:port
cf ssh mendelson-as2 -L 5432:postgres-6d0b82f6-568c-4e83-be41-0c0af924e5d3.crkc3ulytfr9.eu-central-1.rds.amazonaws.com:8062
