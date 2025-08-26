$(document).ready(function () {

    const now = new Date();
    var minutes = now.getMinutes();
      snowEffectBind(1); 
    if (minutes % 2 == 0) {
        //  snowEffectBind(1); 
    } else {
        //  snowEffectBind(2); 
    }
});

//Khởi tạo 1 số biến
var SCREEN_WIDTH = window.innerWidth;
var SCREEN_HEIGHT = window.innerHeight;
var container;
var particle;
var camera;
var scene;
var renderer;
var mouseX = 0;
var mouseY = 0;
var windowHalfX = window.innerWidth / 2;
var windowHalfY = window.innerHeight / 2;
var particles = [];
var particleImage = new Image();
particleImage.src = 'https://ngocrongonline.com//images/snow2.png';
//     var particleImage2 = new Image();
// particleImage2.src = 'https://forum.ngocrongonline.com/js/211.png';
// var particleImage3 = new Image();
// particleImage3.src = 'https://forum.ngocrongonline.com/js/212.png';
// var particleImage4 = new Image();
// particleImage4.src = 'https://forum.ngocrongonline.com/js/213.png';

function snowEffectBind(id) {
    container = $('.snowEffect');

    camera = new THREE.PerspectiveCamera(75, SCREEN_WIDTH / SCREEN_HEIGHT, 1, 10000);
    camera.position.z = 1000;

    scene = new THREE.Scene();
    scene.add(camera);

    renderer = new THREE.CanvasRenderer();
    renderer.setSize(SCREEN_WIDTH, SCREEN_HEIGHT);
    if (id === 1) {
        var material = new THREE.ParticleBasicMaterial({ map: new THREE.Texture(particleImage) });
    } else {
        var material = new THREE.ParticleBasicMaterial({ map: new THREE.Texture(particleImage2) });

    }

    if (id === 1) {
        var material = new THREE.ParticleBasicMaterial({ map: new THREE.Texture(particleImage) });
        for (var i = 0; i < 50 ; i++) {
            particle = new Particle3D(material);
            particle.position.x = Math.random() * 2000 - 1000;
            particle.position.y = Math.random() * 2000 - 1000;
            particle.position.z = Math.random() * 2000 - 1000;
            particle.scale.x = particle.scale.y = 1;
            scene.add(particle);

            particles.push(particle);
        }
    } else {
        var material2 = new THREE.ParticleBasicMaterial({ map: new THREE.Texture(particleImage2) });
        var material3 = new THREE.ParticleBasicMaterial({ map: new THREE.Texture(particleImage3) });
        var material4 = new THREE.ParticleBasicMaterial({ map: new THREE.Texture(particleImage4) });
        for (var i = 0; i < 15; i++) {
            particle = new Particle3D(material2);
            particle.position.x = Math.random() * 2000 - 1000;
            particle.position.y = Math.random() * 2000 - 1000;
            particle.position.z = Math.random() * 2000 - 1000;
            particle.scale.x = particle.scale.y = 1;
            scene.add(particle);

            particles.push(particle);

        }
        for (var i = 0; i < 15; i++) {
            particle = new Particle3D(material3);
            particle.position.x = Math.random() * 2000 - 1000;
            particle.position.y = Math.random() * 2000 - 1000;
            particle.position.z = Math.random() * 2000 - 1000;
            particle.scale.x = particle.scale.y = 1;
            scene.add(particle);

            particles.push(particle);

        }
        for (var i = 0; i < 15; i++) {
            particle = new Particle3D(material4);
            particle.position.x = Math.random() * 2000 - 1000;
            particle.position.y = Math.random() * 2000 - 1000;
            particle.position.z = Math.random() * 2000 - 1000;
            particle.scale.x = particle.scale.y = 1;
            scene.add(particle);

            particles.push(particle);

        }
    }
    // for (var i = 0; i < 25; i++) {
    //     particle = new Particle3D(material2);
    //     particle.position.x = Math.random() * 2000 - 500;
    //     particle.position.y = Math.random() * 2000 - 500;
    //     particle.position.z = Math.random() * 2000 - 500;
    //     particle.scale.x = particle.scale.y = 1;
    //     scene.add(particle);

    //     particles.push(particle);
    // }

    container.html(renderer.domElement);

    document.addEventListener('mousemove', onDocumentMouseMove, false);
    document.addEventListener('touchstart', onDocumentTouchStart, false);
    document.addEventListener('touchmove', onDocumentTouchMove, false);

    setInterval(loop, 1000 / 35);
}

function onDocumentMouseMove(event) {
    mouseX = event.clientX - windowHalfX;
    mouseY = event.clientY - windowHalfY;
}

function onDocumentTouchStart(event) {
    if (event.touches.length == 1) {
        event.preventDefault();

        mouseX = event.touches[0].pageX - windowHalfX;
        mouseY = event.touches[0].pageY - windowHalfY;
    }
}

function onDocumentTouchMove(event) {
    if (event.touches.length == 1) {
        event.preventDefault();

        mouseX = event.touches[0].pageX - windowHalfX;
        mouseY = event.touches[0].pageY - windowHalfY;
    }
}

function loop() {
    for (var i = 0; i < particles.length; i++) {
        var particle = particles[i];
        particle.updatePhysics();

        with (particle.position) {
            if (y < -1000) y += 2000;
            if (x > 1000) x -= 2000;
            else if (x < -1000) x += 2000;
            if (z > 1000) z -= 2000;
            else if (z < -1000) z += 2000;
        }
    }

    camera.position.x += (mouseX - camera.position.x) * 0.05;
    camera.position.y += (-mouseY - camera.position.y) * 0.05;
    camera.lookAt(scene.position);

    renderer.render(scene, camera);
}