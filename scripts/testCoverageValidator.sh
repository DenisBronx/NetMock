#!/bin/bash

set -e

# Arg Names
# -c Class
# -m Method
# -b Branch
# -l Line
# -i Instruction

# Default values for arguments
MIN_ACCEPTABLE_CLASS_COVERAGE=${1:-100}
MIN_ACCEPTABLE_METHOD_COVERAGE=${2:-100}
MIN_ACCEPTABLE_BRANCH_COVERAGE=${3:-100}
MIN_ACCEPTABLE_LINE_COVERAGE=${4:-100}
MIN_ACCEPTABLE_INSTRUCTION_COVERAGE=${5:-100}

print_arg_not_found_message() {
  ARG=$1
  echo "Arg not found: '$ARG'. Use args defined in ./scripts/testCoverageValidator.sh" 1>&2
  exit 1
}

# Parse named arguments
while getopts ":c:m:b:l:i:" opt; do
  case ${opt} in
  c)
    MIN_ACCEPTABLE_CLASS_COVERAGE=$OPTARG
    ;;
  m)
    MIN_ACCEPTABLE_METHOD_COVERAGE=$OPTARG
    ;;
  b)
    MIN_ACCEPTABLE_BRANCH_COVERAGE=$OPTARG
    ;;
  l)
    MIN_ACCEPTABLE_LINE_COVERAGE=$OPTARG
    ;;
  i)
    MIN_ACCEPTABLE_INSTRUCTION_COVERAGE=$OPTARG
    ;;
  *)
    print_arg_not_found_message "$OPTARG"
    ;;
  esac
done
shift $((OPTIND - 1))

COVERAGE_REPORT="build/reports/kover/report.xml"

get_overall_report_tag() {
  echo "//report[1]"
}

get_covered_number_of_type() {
  xmlstarlet sel -t -m "$(get_overall_report_tag)" \
    -v "counter[@type='$1']/@covered" \
    -n "$COVERAGE_REPORT"
}

get_missed_number_of_type() {
  xmlstarlet sel -t -m "$(get_overall_report_tag)" \
    -v "counter[@type='$1']/@missed" \
    -n "$COVERAGE_REPORT"
}

get_coverage_percentage() {
  total=$(echo "$1 + $2" | bc)
  ratio=$(echo "scale=3; $1 / $total" | bc)
  printf "%.1f\n" "$(echo "$ratio * 100" | bc)"
}

get_coverage_report_percentage() {
  counterType=$1
  covered=$(get_covered_number_of_type "$counterType")
  missed=$(get_missed_number_of_type "$counterType")
  get_coverage_percentage "$covered" "$missed"
}

validate_coverage_with_message() {
  currentCoverage=$1
  minAcceptableCoverage=$2
  coverageType=$3
  echo "Coverage for $coverageType: $currentCoverage%"
  if (($(echo "$currentCoverage < $minAcceptableCoverage" | bc -l))); then
    echo "$coverageType coverage is below the required threshold of $minAcceptableCoverage%."
    exit 1
  fi
}

validate_line_coverage() {
  lineCoverage=$(get_coverage_report_percentage "LINE")
  validate_coverage_with_message "$lineCoverage" "$1" "Line"
}

validate_branch_coverage() {
  branchCoverage=$(get_coverage_report_percentage "BRANCH")
  validate_coverage_with_message "$branchCoverage" "$1" "Branch"
}

validate_method_coverage() {
  methodCoverage=$(get_coverage_report_percentage "METHOD")
  validate_coverage_with_message "$methodCoverage" "$1" "Method"
}

validate_class_coverage() {
  classCoverage=$(get_coverage_report_percentage "CLASS")
  validate_coverage_with_message "$classCoverage" "$1" "Class"
}

validate_instruction_coverage() {
  instructionCoverage=$(get_coverage_report_percentage "INSTRUCTION")
  validate_coverage_with_message "$instructionCoverage" "$1" "Instruction"
}

validate_test_coverage() {
  validate_class_coverage "$MIN_ACCEPTABLE_CLASS_COVERAGE"
  validate_method_coverage "$MIN_ACCEPTABLE_METHOD_COVERAGE"
  validate_branch_coverage "$MIN_ACCEPTABLE_BRANCH_COVERAGE"
  validate_line_coverage "$MIN_ACCEPTABLE_LINE_COVERAGE"
  validate_instruction_coverage "$MIN_ACCEPTABLE_INSTRUCTION_COVERAGE"
  echo "All coverage thresholds are met."
  exit 0
}

validate_test_coverage

exit 1
