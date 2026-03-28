#!/bin/bash
# Script to remove all multi-language support files
# This will delete all ResourceBundle files for German (de), Spanish (es),
# French (fr), Italian (it), and Portuguese (pt)

set -e

echo "=========================================="
echo "Multi-Language Files Removal Script"
echo "=========================================="
echo ""

# Define the base directory
BASE_DIR="/Users/I572958/SAPDevelop/github/mend-as2/src"

# Count files before deletion
echo "Counting files to be deleted..."
TOTAL_DE=$(find "$BASE_DIR" -type f -name "*_de.java" | wc -l | tr -d ' ')
TOTAL_ES=$(find "$BASE_DIR" -type f -name "*_es.java" | wc -l | tr -d ' ')
TOTAL_FR=$(find "$BASE_DIR" -type f -name "*_fr.java" | wc -l | tr -d ' ')
TOTAL_IT=$(find "$BASE_DIR" -type f -name "*_it.java" | wc -l | tr -d ' ')
TOTAL_PT=$(find "$BASE_DIR" -type f -name "*_pt.java" | wc -l | tr -d ' ')
TOTAL=$((TOTAL_DE + TOTAL_ES + TOTAL_FR + TOTAL_IT + TOTAL_PT))

echo ""
echo "Files to be deleted:"
echo "  German (de):     $TOTAL_DE files"
echo "  Spanish (es):    $TOTAL_ES files"
echo "  French (fr):     $TOTAL_FR files"
echo "  Italian (it):    $TOTAL_IT files"
echo "  Portuguese (pt): $TOTAL_PT files"
echo "  ----------------------------"
echo "  TOTAL:           $TOTAL files"
echo ""

read -p "Do you want to proceed with deletion? (yes/no): " CONFIRM
if [ "$CONFIRM" != "yes" ]; then
    echo "Deletion cancelled."
    exit 0
fi

echo ""
echo "Creating backup list of files to be deleted..."
mkdir -p backup
find "$BASE_DIR" -type f -name "*_de.java" -o -name "*_es.java" -o -name "*_fr.java" -o -name "*_it.java" -o -name "*_pt.java" > backup/deleted_files_list.txt

echo "Deleting German (de) files..."
find "$BASE_DIR" -type f -name "*_de.java" -delete

echo "Deleting Spanish (es) files..."
find "$BASE_DIR" -type f -name "*_es.java" -delete

echo "Deleting French (fr) files..."
find "$BASE_DIR" -type f -name "*_fr.java" -delete

echo "Deleting Italian (it) files..."
find "$BASE_DIR" -type f -name "*_it.java" -delete

echo "Deleting Portuguese (pt) files..."
find "$BASE_DIR" -type f -name "*_pt.java" -delete

echo ""
echo "=========================================="
echo "Deletion completed!"
echo "=========================================="
echo ""
echo "$TOTAL multi-language files have been deleted."
echo ""
echo "Backup information:"
echo "  List of deleted files: backup/deleted_files_list.txt"
echo ""
echo "Next steps:"
echo "  1. Rebuild the project: mvn clean compile"
echo "  2. Test the application with English locale only"
echo "  3. If needed, restore from git: git checkout -- src/"
echo ""
