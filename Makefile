.PHONY: dev
dev:
	clj -M --main cljs.main --compile hello-world.core --repl

.PHONY: build
build:
	clj -M --main cljs.main --optimizations advanced --compile hello-world.core
