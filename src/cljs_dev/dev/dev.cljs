(ns dev.dev
  (:require [potoo.core]
            [figwheel.client :as fw]))

(fw/start {:websocket-url "ws://localhost:3449/figwheel-ws"})