import sys


def main():
    if len(sys.argv) == 1:
        print("No output path specified")
        exit(1)
    output_path = sys.argv[1]
    print("Running Python Data Generator Script")
    print("Output Path: " + output_path)


if __name__ == "__main__":
    main()
